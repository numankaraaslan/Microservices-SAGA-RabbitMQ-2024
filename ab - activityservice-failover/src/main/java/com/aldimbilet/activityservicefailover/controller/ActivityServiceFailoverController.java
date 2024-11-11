package com.aldimbilet.activityservicefailover.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

// All activityservice endpoints are forwarded here by gateway routeconfig
// activity-failover is the only service and the endpoint to return unauthorized or inaccessible errors to mvc app
@RestController
public class ActivityServiceFailoverController
{
	@GetMapping(path = "activity-failover")
	public ResponseEntity<Object> activityservicefailget()
	{
		ResponseEntity<Object> entity = new ResponseEntity<>("activity service is down, please wait", HttpStatus.SERVICE_UNAVAILABLE);
		return entity;
	}

	@PostMapping(path = "activity-failover")
	public ResponseEntity<Object> activityservicefailpost()
	{
		ResponseEntity<Object> entity = new ResponseEntity<>("activity service is down, please wait", HttpStatus.SERVICE_UNAVAILABLE);
		return entity;
	}
}
