package com.aldimbilet.userservice.config;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.aldimbilet.util.Constants;
import com.aldimbilet.util.JWTUtils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthorizationFilter extends OncePerRequestFilter
{
	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException
	{
		String header = req.getHeader(Constants.HEADER_STRING);
		if (header == null || !header.startsWith(Constants.TOKEN_PREFIX))
		{
			// If no header for Authorization, go ahead and invoke the next filter of tomcat or spring or something
			chain.doFilter(req, res);
			return;
		}
		// the method below retrieves the UsernamePasswordAuthenticationToken of the spring framework from the header of the request
		UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
		// The next filter (probably spring security filter somewhere) should get the userpassauthtoken thingy from the context
		// This could ve a header or some request body or something else depending on the httpservletrequest structure
		SecurityContextHolder.getContext().setAuthentication(authentication);
		// Do the next filter, we injected jwt filter to get it from header and to validate and to put it in the context
		chain.doFilter(req, res);
	}

	// Reads the JWT from the Authorization header, and then uses JWT to validate the token
	// Nothing magic
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request)
	{
		String token = request.getHeader(Constants.HEADER_STRING);
		if (token != null)
		{
			// parse the token.
			String user = JWT.require(Algorithm.HMAC512(JWTUtils.SECRET_KEY.getBytes())).build().verify(token.replace(Constants.TOKEN_PREFIX, "")).getSubject();
			if (user != null)
			{
				// new arraylist means authorities or roles
				return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<GrantedAuthority>());
			}
			return null;
		}
		return null;
	}
}