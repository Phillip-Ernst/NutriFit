package com.phillipe.NutriFit.repository;

import com.phillipe.NutriFit.model.entity.BodyMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BodyMeasurementRepository extends JpaRepository<BodyMeasurement, Long> {
    List<BodyMeasurement> findByUserIdOrderByRecordedAtDesc(Long userId);
    Optional<BodyMeasurement> findFirstByUserIdOrderByRecordedAtDesc(Long userId);
    Optional<BodyMeasurement> findByIdAndUserId(Long id, Long userId);
}
