package com.phillipe.NutriFit.repository;

import com.phillipe.NutriFit.model.entity.WorkoutPlanDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkoutPlanDayRepository extends JpaRepository<WorkoutPlanDay, Long> {
    Optional<WorkoutPlanDay> findByIdAndWorkoutPlanUserId(Long id, Long userId);
}
