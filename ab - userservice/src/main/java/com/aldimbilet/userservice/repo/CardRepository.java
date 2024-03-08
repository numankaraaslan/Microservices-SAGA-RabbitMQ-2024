package com.aldimbilet.userservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aldimbilet.userservice.model.CardInfo;

@Repository
public interface CardRepository extends JpaRepository<CardInfo, Long>
{
	CardInfo findByUserId(Long userId);
}
