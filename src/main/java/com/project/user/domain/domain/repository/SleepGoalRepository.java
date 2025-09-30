package com.project.user.domain.domain.repository;

import com.project.user.domain.domain.entity.SleepGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SleepGoalRepository extends JpaRepository<SleepGoal, Long> {
    Optional<SleepGoal> findByUserNo(Long userNo);
}