package com.aldimbilet.activityservicefailover.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

// All paymentservice endpoints are forwarded here by gateway routeconfig
// payment-failover is the only service and the endpoint to return unauthorized or inaccessible errors to mvc app
@RestController
public class PaymentServiceFailoverController
{
	@GetMapping(path = "payment-failover")
	public ResponseEntity<Object> paymentservicefailget()
	{
		return ResponseEntity.ok("payment service is down, please wait");
	}

	@PostMapping(path = "payment-failover")
	public ResponseEntity<Object> paymentservicefailpost()
	{
		return ResponseEntity.ok("payment service is down, please wait");
	}
}
