package com.aldimbilet.activityservicefailover.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

// All paymentservice endpoints are forwarded here by gateway routeconfig
// payment-failover is the only service and the endpoint to return unauthorized or inaccessible errors to mvc app
@RestController
public class PaymentServiceFailoverController
{
	@RequestMapping(path = "payment-failover", method = RequestMethod.GET, consumes = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<Object> userServiceFails()
	{
		ResponseEntity<Object> entity = new ResponseEntity<>("payment service is down, please wait", HttpStatus.SERVICE_UNAVAILABLE);
		return entity;
	}

	@RequestMapping(path = "payment-failover", method = RequestMethod.POST, consumes = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<Object> userServiceFails(@RequestBody String body)
	{
		ResponseEntity<Object> entity = new ResponseEntity<>("payment service is down, please wait", HttpStatus.SERVICE_UNAVAILABLE);
		return entity;
	}
}
