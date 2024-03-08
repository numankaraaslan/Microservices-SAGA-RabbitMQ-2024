package com.aldimbilet.userservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aldimbilet.userservice.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>
{
}