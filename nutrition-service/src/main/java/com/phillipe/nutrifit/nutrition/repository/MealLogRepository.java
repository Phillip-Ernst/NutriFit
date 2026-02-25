package com.phillipe.nutrifit.nutrition.repository;

import com.phillipe.nutrifit.nutrition.model.entity.MealLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MealLogRepository extends JpaRepository<MealLog, Long> {
    List<MealLog> findByUsernameOrderByCreatedAtDesc(String username);
    Optional<MealLog> findByIdAndUsername(Long id, String username);
}