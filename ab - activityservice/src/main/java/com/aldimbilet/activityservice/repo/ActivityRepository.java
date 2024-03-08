package com.aldimbilet.activityservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aldimbilet.activityservice.model.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long>
{
}