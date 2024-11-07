package com.aldimbilet.userservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aldimbilet.userservice.model.ABUser;
import com.aldimbilet.userservice.repo.UserRepository;

@Service
public class UserService implements UserDetailsService
{
	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username)
	{
		return userRepository.findByUsername(username);
	}

	public boolean save(ABUser user)
	{
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setPasswordConfirm(bCryptPasswordEncoder.encode(user.getPassword()));
		return userRepository.save(user) != null;
	}

	public ABUser findByUsername(String username)
	{
		return userRepository.findByUsername(username);
	}

	public ABUser findById(Long userId)
	{
		return userRepository.findById(userId).orElse(null);
	}

	public List<ABUser> getAllUsers()
	{
		return userRepository.findAll();
	}
}
