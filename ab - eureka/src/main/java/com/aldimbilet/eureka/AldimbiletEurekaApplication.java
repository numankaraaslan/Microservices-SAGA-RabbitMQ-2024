package com.aldimbilet.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

// @EnableEurekaServer is the only necessary annotation for a basic eureka server
// http://localhost:8761
@EnableEurekaServer
@SpringBootApplication
public class AldimbiletEurekaApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(AldimbiletEurekaApplication.class, args);
	}
}
