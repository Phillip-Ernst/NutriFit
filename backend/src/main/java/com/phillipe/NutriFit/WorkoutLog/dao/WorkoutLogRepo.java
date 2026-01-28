package com.phillipe.NutriFit.WorkoutLog.dao;

import com.phillipe.NutriFit.WorkoutLog.model.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutLogRepo extends JpaRepository<WorkoutLog, Long> {
    List<WorkoutLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}
