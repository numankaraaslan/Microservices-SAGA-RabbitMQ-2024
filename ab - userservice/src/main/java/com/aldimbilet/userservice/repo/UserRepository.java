package com.aldimbilet.userservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aldimbilet.userservice.model.ABUser;

public interface UserRepository extends JpaRepository<ABUser, Long>
{
	ABUser findByUsername(String username);
}
