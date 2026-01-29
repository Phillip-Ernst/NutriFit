package com.phillipe.NutriFit.WorkoutPlan.dao;

import com.phillipe.NutriFit.WorkoutPlan.model.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutPlanRepo extends JpaRepository<WorkoutPlan, Long> {
    List<WorkoutPlan> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<WorkoutPlan> findByIdAndUserId(Long id, Long userId);
}
