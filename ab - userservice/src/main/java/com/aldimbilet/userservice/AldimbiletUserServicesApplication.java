package com.aldimbilet.userservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.aldimbilet.userservice.model.ABUser;
import com.aldimbilet.userservice.model.CardInfo;
import com.aldimbilet.userservice.model.Role;
import com.aldimbilet.userservice.repo.CardRepository;
import com.aldimbilet.userservice.repo.RoleRepository;
import com.aldimbilet.userservice.service.UserService;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class AldimbiletUserServicesApplication
{
	@Autowired
	UserService userService;

	@Autowired
	CardRepository cardRepo;

	@Autowired
	RoleRepository roleRepository;

	public static void main(String[] args)
	{
		SpringApplication.run(AldimbiletUserServicesApplication.class, args);
	}

	@PostConstruct
	private void saveDefaultUser()
	{
		// prepare a test user and card info
		if (userService.findByUsername("user") == null)
		{
			Role role1 = new Role();
			role1.setName("ADMIN");
			roleRepository.save(role1);
			List<Role> roles = new ArrayList<>();
			roles.add(role1);
			ABUser user = new ABUser();
			user.setName("user");
			user.setUsername("user");
			user.setPassword("1234");
			user.setSurname("asd");
			user.setPasswordConfirm("1234");
			user.setEmail("user@email.com");
			user.setRoles(roles);
			userService.save(user);
		}
		try
		{
			CardInfo card = new CardInfo();
			card.setUserId((long) 1);
			card.setCardNumber("123123123123");
			cardRepo.save(card);
		}
		catch (Exception e)
		{
		}
	}
}
