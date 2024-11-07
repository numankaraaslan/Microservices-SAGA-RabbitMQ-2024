package com.aldimbilet.eureka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SeConfig
{
	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception
	{
		// CSRF disable to be able to pass jwt tokens through this
		// Any request to eureka must set username and password for eureka
		http.csrf(c -> c.disable());
		http.formLogin(c -> c.defaultSuccessUrl("/"));
		// management page is secured
		http.authorizeHttpRequests(c -> c.anyRequest().authenticated());
		return http.build();
	}
}
