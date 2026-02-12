package com.phillipe.NutriFit.repository;

import com.phillipe.NutriFit.model.entity.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {
    List<WorkoutLog> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<WorkoutLog> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Query("UPDATE WorkoutLog wl SET wl.workoutPlanDay = null WHERE wl.workoutPlanDay.id IN :dayIds")
    void clearWorkoutPlanDayReferences(@Param("dayIds") Collection<Long> dayIds);
}
