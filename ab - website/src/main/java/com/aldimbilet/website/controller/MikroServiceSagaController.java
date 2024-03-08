package com.aldimbilet.website.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aldimbilet.pojos.ActivityPojo;
import com.aldimbilet.pojos.BasketPojo;
import com.aldimbilet.pojos.CardInfoPojo;
import com.aldimbilet.pojos.UserInfoPojo;
import com.aldimbilet.pojos.UserRegisterPojo;
import com.aldimbilet.util.Constants;
import com.aldimbilet.website.feign.ActivityClient;
import com.aldimbilet.website.feign.PaymentClient;
import com.aldimbilet.website.feign.UserClient;
import com.aldimbilet.website.util.SessionConstants;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MikroServiceSagaController
{
	private UserClient userClient;

	private ActivityClient activityClient;

	private PaymentClient paymentClient;

	// See RabbitConfig
	private RabbitTemplate template;

	// I have added the names here to make it easy to track inside the code
	@Value("emailReceiptTopic")
	private TopicExchange emailReceiptTopic;

	// I have added the names here to make it easy to track inside the code
	@Value("emailCancelationDirect")
	private DirectExchange emailCancelationDirect;

	public MikroServiceSagaController(UserClient userClient, ActivityClient activityClient, PaymentClient paymentClient, RabbitTemplate template, TopicExchange emailReceiptTopic, DirectExchange emailCancelationDirect)
	{
		this.userClient = userClient;
		this.activityClient = activityClient;
		this.paymentClient = paymentClient;
		this.template = template;
		this.emailReceiptTopic = emailReceiptTopic;
		this.emailCancelationDirect = emailCancelationDirect;
	}

	@GetMapping(path = "login")
	public ModelAndView login()
	{
		ModelAndView login = new ModelAndView("login");
		return login;
	}

	@PostMapping(path = "login")
	public ModelAndView login(HttpServletRequest req)
	{
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		// Quick and dirty way of creating the necessary object for spring security
		// This is basically a json object, could have been mapped from spring security User class to json with jackson
		// I'm lazy
		String user = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
		String token = "";
		try
		{
			// the response is determined by userservice
			// you will implement some logic according to the return value
			// it could be some entity inside the responseentity or a whole other data structure of your imagination
			// just make sure this communication is documented somewhere
			ResponseEntity<String> responseEntity = userClient.login(user);
			token = responseEntity.getBody();
			String bearer = token.substring(token.indexOf(" ") + 1);
			user = token.substring(token.indexOf("(") + 1, token.indexOf(")"));
			req.getSession().setAttribute(SessionConstants.BEARER, bearer);
			req.getSession().setAttribute(SessionConstants.USERNAME, user);
			return new ModelAndView("redirect:/index");
		}
		catch (FeignException e)
		{
			// Each exception must mean something different
			// Throw them carefully from the services
			if (e.status() == HttpStatus.SERVICE_UNAVAILABLE.value())
			{
				ModelAndView login = new ModelAndView("login");
				// userservice failover must have been invoked
				login.addObject("info", StandardCharsets.UTF_8.decode(e.responseBody().get()).toString());
				return login;
			}
			else if (e.status() == HttpStatus.UNAUTHORIZED.value())
			{
				return new ModelAndView("redirect:/login?err=3");
			}
			else
			{
				return new ModelAndView("redirect:/login");
			}
		}
	}

	@GetMapping(path = "signup")
	public ModelAndView signup()
	{
		ModelAndView signup = new ModelAndView("signup");
		// userregisterpojo object for thymeleaf form
		UserRegisterPojo pojo = new UserRegisterPojo();
		// some default values for ease
		pojo.setEmail("asd@asd.com");
		pojo.setName("user");
		pojo.setSurname("asd");
		pojo.setUsername("user" + new Random().nextInt(100));
		signup.addObject("userregisterpojo", pojo);
		return signup;
	}

	@PostMapping(path = "signup")
	public ModelAndView signup(@ModelAttribute(name = "userregisterpojo") UserRegisterPojo pojo)
	{
		try
		{
			userClient.register(pojo);
			return new ModelAndView("redirect:/login");
		}
		catch (FeignException e)
		{
			// Feign considers httpresponses other than 200 are all exceptions
			// You can write a custom error decoder for feign and throw your own exceptions by grouping them together
			// This is a very basic fault tolerance built upon feign
			if (e.status() == HttpStatus.SERVICE_UNAVAILABLE.value())
			{
				ModelAndView signup = new ModelAndView("signup");
				// userservice failover must have been invoked
				signup.addObject("info", StandardCharsets.UTF_8.decode(e.responseBody().get()).toString());
				return signup;
			}
			else
			{
				// General errors
				return new ModelAndView("redirect:/signup?err=1");
			}
		}
	}

	@GetMapping(path = "logout")
	public ModelAndView logout(HttpServletRequest req)
	{
		req.getSession().removeAttribute(SessionConstants.USERNAME);
		req.getSession().removeAttribute(SessionConstants.BEARER);
		return new ModelAndView("redirect:/index");
	}

	@GetMapping(path = "payment")
	public ModelAndView payment(HttpServletRequest req)
	{
		ModelAndView payment = new ModelAndView("payment");
		// This is the fault tolerance from business aspect
		// In order to prevent spamming, there is no reservation logic
		// If all seats are taken before you buy the ticket, you will get an error
		// You can't just reserve a seat and occupy some space
		BasketPojo basket = (BasketPojo) req.getSession().getAttribute(SessionConstants.BASKET);
		String bearer = (String) req.getSession().getAttribute(SessionConstants.BEARER);
		String cardInfo = req.getSession().getAttribute(SessionConstants.CARD_NUMBER).toString();
		Boolean sold = false;
		try
		{
			sold = paymentClient.makePayment(Constants.TOKEN_PREFIX + bearer, cardInfo).getBody();
			// makePayment randomly returns true or false, simulating credit card denials or various issues
			if (sold)
			{
				Boolean resp = activityClient.checkActivitySeatAvailable(Constants.TOKEN_PREFIX + bearer, basket.getActId()).getBody();
				if (!resp)
				{
					payment.addObject("status", "Event is full, sorry");
					// Returning payment is another aspect of fault tolerance
					paymentClient.returnPayment(Constants.TOKEN_PREFIX + bearer, cardInfo);
				}
				else
				{
					activityClient.sellSeat(Constants.TOKEN_PREFIX + bearer, basket.getActId());
					payment.addObject("status", "Payment done, you bought the ticket. You will receive an email of the receipt.");
					UserInfoPojo infoPojo = (UserInfoPojo) req.getSession().getAttribute(SessionConstants.USERPOJO);
					// To simulate different routing keys reaching to same queue
					if (infoPojo.getId() % 2 == 0)
					{
						// Make sure you have a converter bean defined somewhere to send custom classes
						// The topic exchange listens for routing keys with a format
						// Here it is "email.receipt.*" so "email.receipt.special" and "email.receipt.normal" will go to the same topic
						template.convertAndSend(emailReceiptTopic.getName(), "email.receipt.special", infoPojo);
					}
					else
					{
						template.convertAndSend(emailReceiptTopic.getName(), "email.receipt.normal", infoPojo);
					}
				}
			}
			else
			{
				payment.addObject("status", "Payment error, sorry");
			}
		}
		catch (FeignException e)
		{
			if (e.status() == HttpStatus.SERVICE_UNAVAILABLE.value())
			{
				// 503
				payment.addObject("status", "Payment service is not accasible");
			}
		}
		return payment;
	}

	@GetMapping(path = "checkout")
	public ModelAndView checkout(HttpServletRequest req)
	{
		ModelAndView checkout = new ModelAndView("checkout");
		ActivityPojo activityPojo = (ActivityPojo) req.getSession().getAttribute(SessionConstants.ACTIVITY_POJO);
		BasketPojo basket = (BasketPojo) req.getSession().getAttribute(SessionConstants.BASKET);
		String username = req.getSession().getAttribute(SessionConstants.USERNAME).toString();
		String activityname = activityPojo.getName();
		String bearer = (String) req.getSession().getAttribute(SessionConstants.BEARER);
		HttpEntity<CardInfoPojo> resp = userClient.getUserCard(Constants.TOKEN_PREFIX + bearer, basket.getUserId());
		String cardnumber = resp.getBody().getCardNumber();
		req.getSession().setAttribute(SessionConstants.CARD_NUMBER, cardnumber);
		checkout.addObject("username", username);
		checkout.addObject("activityname", activityname);
		checkout.addObject("cardnumber", cardnumber);
		return checkout;
	}

	@GetMapping(path = "eventdetails")
	public ModelAndView eventdetails(HttpServletRequest req)
	{
		ModelAndView eventdetails = new ModelAndView("eventdetails");
		Long actId = Long.parseLong(req.getParameter("id"));
		UserInfoPojo userPojo = (UserInfoPojo) req.getSession().getAttribute(SessionConstants.USERPOJO);
		ActivityPojo activityPojo = activityClient.getActivityInfo(actId).getBody();
		req.getSession().setAttribute(SessionConstants.ACTIVITY_POJO, activityPojo);
		Long userId = userPojo.getId();
		BasketPojo basket = new BasketPojo();
		basket.setActId(actId);
		basket.setUserId(userId);
		req.getSession().setAttribute(SessionConstants.BASKET, basket);
		String username = userPojo.getName();
		String eventname = activityPojo.getName();
		eventdetails.addObject("username", username);
		eventdetails.addObject("eventname", eventname);
		return eventdetails;
	}

	@GetMapping(path = "events")
	public ModelAndView events()
	{
		ModelAndView events = new ModelAndView("events");
		try
		{
			List<ActivityPojo> activities = activityClient.getActivities().getBody();
			events.addObject("activities", activities);
		}
		catch (FeignException e)
		{
			if (e.status() == HttpStatus.SERVICE_UNAVAILABLE.value())
			{
				// 503
				events.addObject("info", "Activity service is not accasible");
			}
		}
		return events;
	}

	@PostMapping(path = "admin")
	public ModelAndView admin(HttpServletRequest req, @RequestParam(name = "actid") Integer actid)
	{
		String bearer = (String) req.getSession().getAttribute(SessionConstants.BEARER);
		List<UserInfoPojo> resp = userClient.getAllUsers(Constants.TOKEN_PREFIX + bearer).getBody();
		for (UserInfoPojo userInfoPojo : resp)
		{
			// correlationData is required with a unique identifier to be able to get ack info
			CorrelationData correlationData = new CorrelationData(userInfoPojo.getId().toString());
			// Cancelation email queue is bounded to a directexchange
			// Meaning it will only accept exact matches of "email.cancelation"
			// You just have to mention exchange name and the related binding, the rest is up to rabbitmq
			template.convertAndSend(emailCancelationDirect.getName(), "email.cancelation", userInfoPojo, correlationData);
		}
		return new ModelAndView("redirect:/admin?canceled=" + actid);
	}

	@GetMapping(path = "admin")
	public ModelAndView admin(@RequestParam(name = "canceled", required = false) Integer actid)
	{
		List<ActivityPojo> activities = activityClient.getActivities().getBody();
		ModelAndView events = new ModelAndView("admin");
		events.addObject("activities", activities);
		return events;
	}

	@GetMapping(path = { "", "index" })
	public ModelAndView index(HttpServletRequest req)
	{
		// Normally there will be a logic for secure or free services
		// Free endpoints will not require jwt headers and they won't require @RequestHeader in @FeignClient classes
		String bearer = (String) req.getSession().getAttribute(SessionConstants.BEARER);
		String username = (String) req.getSession().getAttribute(SessionConstants.USERNAME);
		String userInfo = "", userString = "", actString = "";
		if (username != null)
		{
			ResponseEntity<UserInfoPojo> resp = null;
			try
			{
				// Constants.TOKEN_PREFIX + bearer = "Bearer asdasdqwe123"
				resp = userClient.getUserInfo(Constants.TOKEN_PREFIX + bearer, username);
				if (resp.getStatusCode() == HttpStatus.OK)
				{
					UserInfoPojo infoPojo = resp.getBody();
					userInfo = infoPojo.getName() + " " + infoPojo.getSurname() + " is registered at " + infoPojo.getRegDate();
					req.getSession().setAttribute(SessionConstants.USERPOJO, infoPojo);
				}
				else
				{
					userInfo = "Failed to get user info";
				}
				userString = "From user service port number " + userClient.sayHello(Constants.TOKEN_PREFIX + bearer).getBody();
			}
			catch (FeignException e)
			{
				if (e.status() == HttpStatus.SERVICE_UNAVAILABLE.value())
				{
					// 503
					userString = "User service is not accasible";
				}
			}
			try
			{
				actString = "From activity service port number " + activityClient.sayHello(Constants.TOKEN_PREFIX + bearer).getBody();
			}
			catch (FeignException e)
			{
				if (e.status() == HttpStatus.SERVICE_UNAVAILABLE.value())
				{
					// 503
					actString = "Activity service is not accasible";
				}
			}
		}
		else
		{
			userInfo = "Nobody is logged in yet";
		}
		ModelAndView index = new ModelAndView("index");
		index.addObject("userInfo", userInfo);
		index.addObject("userString", userString);
		index.addObject("actString", actString);
		return index;
	}
}
