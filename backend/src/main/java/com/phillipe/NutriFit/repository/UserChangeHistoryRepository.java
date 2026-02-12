package com.phillipe.NutriFit.repository;

import com.phillipe.NutriFit.model.entity.UserChangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserChangeHistoryRepository extends JpaRepository<UserChangeHistory, Long> {
    List<UserChangeHistory> findByUserIdOrderByChangedAtDesc(Long userId);
}
