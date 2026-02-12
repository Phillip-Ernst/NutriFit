package com.phillipe.NutriFit.repository;

import com.phillipe.NutriFit.model.entity.MealLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MealLogRepository extends JpaRepository<MealLog, Long> {
    List<MealLog> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<MealLog> findByIdAndUserId(Long id, Long userId);
}
