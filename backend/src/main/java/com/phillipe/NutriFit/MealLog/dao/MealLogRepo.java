package com.phillipe.NutriFit.MealLog.dao;

import com.phillipe.NutriFit.MealLog.model.MealLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealLogRepo extends JpaRepository<MealLog, Long> {
    List<MealLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}
