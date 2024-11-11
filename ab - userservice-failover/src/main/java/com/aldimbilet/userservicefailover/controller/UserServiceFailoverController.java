package com.aldimbilet.userservicefailover.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// All userservice endpoints are forwarded here by gateway routeconfig
// user-failover is the only service and the endpoint to return unauthorized or inaccessible errors to mvc app
@RestController
public class UserServiceFailoverController
{
	// mapping redirection is in gateway config
	@GetMapping(path = "user-failover")
	public ResponseEntity<Object> userServiceFails()
	{
		ResponseEntity<Object> entity = new ResponseEntity<>("user service is down, please wait", HttpStatus.SERVICE_UNAVAILABLE);
		return entity;
	}

	@PostMapping(path = "user-failover")
	public ResponseEntity<Object> userServiceFails(@RequestBody Object body)
	{
		ResponseEntity<Object> entity = new ResponseEntity<>("user service is down, please wait", HttpStatus.SERVICE_UNAVAILABLE);
		return entity;
	}
}
