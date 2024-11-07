package com.aldimbilet.eureka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SeConfig
{
	@Bean
	SecurityFilterChain config(HttpSecurity http) throws Exception
	{
		http.csrf(custom -> custom.disable());
		http.authorizeHttpRequests(custom -> custom.anyRequest().authenticated());
		http.formLogin(custom -> custom.defaultSuccessUrl("/"));
		http.logout(custom -> custom.logoutSuccessUrl("/"));
		http.httpBasic(custom -> Customizer.withDefaults());
		return http.build();
	}
}
