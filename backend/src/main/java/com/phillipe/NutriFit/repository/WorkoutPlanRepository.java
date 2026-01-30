package com.phillipe.NutriFit.repository;

import com.phillipe.NutriFit.model.entity.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    List<WorkoutPlan> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<WorkoutPlan> findByIdAndUserId(Long id, Long userId);
}
