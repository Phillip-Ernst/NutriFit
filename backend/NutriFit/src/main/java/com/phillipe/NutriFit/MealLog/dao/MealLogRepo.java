package com.phillipe.NutriFit.dao;

import com.phillipe.NutriFit.model.MealLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealLogRepo extends JpaRepository<MealLog, Long> {
    List<MealLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}
