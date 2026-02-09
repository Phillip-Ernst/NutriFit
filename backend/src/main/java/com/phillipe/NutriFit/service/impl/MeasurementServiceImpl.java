package com.phillipe.NutriFit.service.impl;

import com.phillipe.NutriFit.dto.request.MeasurementRequest;
import com.phillipe.NutriFit.dto.response.MeasurementResponse;
import com.phillipe.NutriFit.model.entity.BodyMeasurement;
import com.phillipe.NutriFit.model.entity.User;
import com.phillipe.NutriFit.repository.BodyMeasurementRepository;
import com.phillipe.NutriFit.repository.UserRepository;
import com.phillipe.NutriFit.service.MeasurementService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MeasurementServiceImpl implements MeasurementService {

    private final BodyMeasurementRepository measurementRepo;
    private final UserRepository userRepo;

    public MeasurementServiceImpl(BodyMeasurementRepository measurementRepo, UserRepository userRepo) {
        this.measurementRepo = measurementRepo;
        this.userRepo = userRepo;
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
        measurementRepo.delete(measurement);
    }

    private User findUser(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }
        return user;
    }
}
