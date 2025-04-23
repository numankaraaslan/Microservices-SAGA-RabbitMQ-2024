package com.aldimbilet.activityservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SeConfig
{
	@Bean
	SecurityFilterChain configure(HttpSecurity http, AuthenticationConfiguration authenticationConfiguration) throws Exception
	{
		// csrf disable to get jwt headers go through
		http.csrf(c -> c.disable());
		http.authorizeHttpRequests(c -> c.requestMatchers("/pay/portinfo").permitAll());
		http.authorizeHttpRequests(c -> c.requestMatchers("/actuator/health/**").permitAll());
		// The rest of the endpoints may or may not require authentication, depends on your business decisions
		http.authorizeHttpRequests(c -> c.anyRequest().authenticated());
		// Add jwt authorization filters inside somwhere of the chain
		http.addFilterBefore(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
		// this disables session creation on Spring Security
		// because microservices are idealy stateless, they are jwt authorized
		http.sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		return http.build();
	}
}
