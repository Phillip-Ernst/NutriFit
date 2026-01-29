package com.phillipe.NutriFit.WorkoutPlan.dao;

import com.phillipe.NutriFit.WorkoutPlan.model.WorkoutPlanDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkoutPlanDayRepo extends JpaRepository<WorkoutPlanDay, Long> {
    Optional<WorkoutPlanDay> findByIdAndWorkoutPlanUserId(Long id, Long userId);
}
