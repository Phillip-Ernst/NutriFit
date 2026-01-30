package com.phillipe.NutriFit.repository;

import com.phillipe.NutriFit.model.entity.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {
    List<WorkoutLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}
