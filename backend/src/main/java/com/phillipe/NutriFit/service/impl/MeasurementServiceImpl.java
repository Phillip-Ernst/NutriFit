package com.phillipe.NutriFit.service.impl;

import com.phillipe.NutriFit.dto.request.MeasurementRequest;
import com.phillipe.NutriFit.dto.response.MeasurementResponse;
import com.phillipe.NutriFit.model.entity.BodyMeasurement;
import com.phillipe.NutriFit.model.entity.User;
import com.phillipe.NutriFit.repository.BodyMeasurementRepository;
import com.phillipe.NutriFit.repository.UserRepository;
import com.phillipe.NutriFit.service.ChangeHistoryService;
import com.phillipe.NutriFit.service.MeasurementService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MeasurementServiceImpl implements MeasurementService {

    private final BodyMeasurementRepository measurementRepo;
    private final UserRepository userRepo;
    private final ChangeHistoryService changeHistoryService;

    public MeasurementServiceImpl(BodyMeasurementRepository measurementRepo, UserRepository userRepo,
                                  ChangeHistoryService changeHistoryService) {
        this.measurementRepo = measurementRepo;
        this.userRepo = userRepo;
        this.changeHistoryService = changeHistoryService;
    }

    @Override
    @Transactional
    public MeasurementResponse createMeasurement(MeasurementRequest request, String username) {
        User user = findUser(username);

        BodyMeasurement measurement = BodyMeasurement.builder()
                .user(user)
                .heightCm(request.getHeightCm())
                .weightKg(request.getWeightKg())
                .bodyFatPercent(request.getBodyFatPercent())
                .neckCm(request.getNeckCm())
                .shouldersCm(request.getShouldersCm())
                .chestCm(request.getChestCm())
                .bicepsCm(request.getBicepsCm())
                .forearmsCm(request.getForearmsCm())
                .waistCm(request.getWaistCm())
                .hipsCm(request.getHipsCm())
                .thighsCm(request.getThighsCm())
                .calvesCm(request.getCalvesCm())
                .notes(request.getNotes())
                .build();

        BodyMeasurement saved = measurementRepo.save(measurement);

        // Record initial values as changes (oldValue=null)
        recordMeasurementChanges(user, saved.getId(), null, saved);

        return MeasurementResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeasurementResponse> getMeasurements(String username) {
        User user = findUser(username);
        return measurementRepo.findByUserIdOrderByRecordedAtDesc(user.getId())
                .stream()
                .map(MeasurementResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MeasurementResponse getLatestMeasurement(String username) {
        User user = findUser(username);
        return measurementRepo.findFirstByUserIdOrderByRecordedAtDesc(user.getId())
                .map(MeasurementResponse::fromEntity)
                .orElse(null);
    }

    @Override
    @Transactional
    public void deleteMeasurement(Long id, String username) {
        User user = findUser(username);
        BodyMeasurement measurement = measurementRepo.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Measurement not found or access denied"));

        // Record deletion (newValue=null)
        recordMeasurementChanges(user, id, measurement, null);

        measurementRepo.delete(measurement);
    }

    private void recordMeasurementChanges(User user, Long entityId, BodyMeasurement oldM, BodyMeasurement newM) {
        recordFieldChange(user, entityId, "heightCm",
                oldM != null ? oldM.getHeightCm() : null,
                newM != null ? newM.getHeightCm() : null);
        recordFieldChange(user, entityId, "weightKg",
                oldM != null ? oldM.getWeightKg() : null,
                newM != null ? newM.getWeightKg() : null);
        recordFieldChange(user, entityId, "bodyFatPercent",
                oldM != null ? oldM.getBodyFatPercent() : null,
                newM != null ? newM.getBodyFatPercent() : null);
        recordFieldChange(user, entityId, "neckCm",
                oldM != null ? oldM.getNeckCm() : null,
                newM != null ? newM.getNeckCm() : null);
        recordFieldChange(user, entityId, "shouldersCm",
                oldM != null ? oldM.getShouldersCm() : null,
                newM != null ? newM.getShouldersCm() : null);
        recordFieldChange(user, entityId, "chestCm",
                oldM != null ? oldM.getChestCm() : null,
                newM != null ? newM.getChestCm() : null);
        recordFieldChange(user, entityId, "bicepsCm",
                oldM != null ? oldM.getBicepsCm() : null,
                newM != null ? newM.getBicepsCm() : null);
        recordFieldChange(user, entityId, "forearmsCm",
                oldM != null ? oldM.getForearmsCm() : null,
                newM != null ? newM.getForearmsCm() : null);
        recordFieldChange(user, entityId, "waistCm",
                oldM != null ? oldM.getWaistCm() : null,
                newM != null ? newM.getWaistCm() : null);
        recordFieldChange(user, entityId, "hipsCm",
                oldM != null ? oldM.getHipsCm() : null,
                newM != null ? newM.getHipsCm() : null);
        recordFieldChange(user, entityId, "thighsCm",
                oldM != null ? oldM.getThighsCm() : null,
                newM != null ? newM.getThighsCm() : null);
        recordFieldChange(user, entityId, "calvesCm",
                oldM != null ? oldM.getCalvesCm() : null,
                newM != null ? newM.getCalvesCm() : null);
    }

    private void recordFieldChange(User user, Long entityId, String field, Object oldVal, Object newVal) {
        if (oldVal != null || newVal != null) {
            changeHistoryService.recordChange(user, "MEASUREMENT", entityId, field, oldVal, newVal);
        }
    }

    private User findUser(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }
        return user;
    }
}
