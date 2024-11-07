package com.aldimbilet.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SeConfig
{
	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain configure(HttpSecurity http, AuthenticationConfiguration authenticationConfiguration) throws Exception
	{
		// csrf disable to get jwt headers go through
		http.csrf(c -> c.disable());
		// login endpoint is free for all
		http.authorizeHttpRequests(c -> c.requestMatchers("/user/login/**").permitAll());
		http.authorizeHttpRequests(c -> c.requestMatchers("/user/register/**").permitAll());
		// The rest of the endpoints may or may not require authentication, depends on your business decisions
		http.authorizeHttpRequests(c -> c.anyRequest().authenticated());
		// Add jwt athentication and authorization filters inside somwhere of the chain
		http.addFilterBefore(new JWTAuthenticationFilter(authenticationConfiguration.getAuthenticationManager()), UsernamePasswordAuthenticationFilter.class);
		// login ' giderken önce token kontrolden geçmesin diye
		http.addFilterAfter(new JWTAuthorizationFilter(), JWTAuthenticationFilter.class);
		// this disables session creation on Spring Security
		// because microservices are idealy stateless, they are jwt authorized
		http.sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		return http.build();
	}
}
