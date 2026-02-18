package com.phillipe.NutriFit.service.impl;

import com.phillipe.NutriFit.repository.UserRepository;
import com.phillipe.NutriFit.repository.WorkoutLogRepository;
import com.phillipe.NutriFit.model.entity.User;
import com.phillipe.NutriFit.repository.WorkoutPlanDayRepository;
import com.phillipe.NutriFit.repository.WorkoutPlanRepository;
import com.phillipe.NutriFit.dto.request.WorkoutPlanDayRequest;
import com.phillipe.NutriFit.dto.request.WorkoutPlanExerciseRequest;
import com.phillipe.NutriFit.dto.request.WorkoutPlanRequest;
import com.phillipe.NutriFit.dto.response.PredefinedExerciseResponse;
import com.phillipe.NutriFit.dto.response.WorkoutPlanDayResponse;
import com.phillipe.NutriFit.dto.response.WorkoutPlanExerciseResponse;
import com.phillipe.NutriFit.dto.response.WorkoutPlanResponse;
import com.phillipe.NutriFit.model.*;
import com.phillipe.NutriFit.model.embedded.WorkoutPlanExercise;
import com.phillipe.NutriFit.model.entity.WorkoutPlan;
import com.phillipe.NutriFit.model.entity.WorkoutPlanDay;
import com.phillipe.NutriFit.service.WorkoutPlanService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class WorkoutPlanServiceImpl implements WorkoutPlanService {

    private final WorkoutPlanRepository workoutPlanRepo;
    private final WorkoutPlanDayRepository workoutPlanDayRepo;
    private final WorkoutLogRepository workoutLogRepo;
    private final UserRepository userRepo;

    public WorkoutPlanServiceImpl(WorkoutPlanRepository workoutPlanRepo,
                                   WorkoutPlanDayRepository workoutPlanDayRepo,
                                   WorkoutLogRepository workoutLogRepo,
                                   UserRepository userRepo) {
        this.workoutPlanRepo = workoutPlanRepo;
        this.workoutPlanDayRepo = workoutPlanDayRepo;
        this.workoutLogRepo = workoutLogRepo;
        this.userRepo = userRepo;
    }

    private User findUserOrThrow(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }
        return user;
    }

    @Override
    @Transactional
    public WorkoutPlanResponse createPlan(WorkoutPlanRequest request, String username) {
        User user = findUserOrThrow(username);

        WorkoutPlan plan = WorkoutPlan.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .build();

        if (request.getDays() != null) {
            for (WorkoutPlanDayRequest dayRequest : request.getDays()) {
                WorkoutPlanDay day = createDayFromRequest(dayRequest);
                plan.addDay(day);
            }
        }

        WorkoutPlan saved = workoutPlanRepo.save(plan);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutPlanResponse> getMyPlans(String username) {
        User user = findUserOrThrow(username);
        return workoutPlanRepo.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutPlanResponse getPlanById(Long id, String username) {
        User user = findUserOrThrow(username);
        WorkoutPlan plan = workoutPlanRepo.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Workout plan not found"));
        return toResponse(plan);
    }

    @Override
    @Transactional
    public WorkoutPlanResponse updatePlan(Long id, WorkoutPlanRequest request, String username) {
        User user = findUserOrThrow(username);
        WorkoutPlan plan = workoutPlanRepo.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Workout plan not found"));

        plan.setName(request.getName());
        plan.setDescription(request.getDescription());

        // Clear FK references in workout_log before removing days (to avoid constraint violation)
        List<Long> existingDayIds = plan.getDays().stream()
                .map(WorkoutPlanDay::getId)
                .filter(dayId -> dayId != null)
                .toList();
        if (!existingDayIds.isEmpty()) {
            workoutLogRepo.clearWorkoutPlanDayReferences(existingDayIds);
        }

        // Clear existing days and add new ones
        plan.getDays().clear();

        if (request.getDays() != null) {
            for (WorkoutPlanDayRequest dayRequest : request.getDays()) {
                WorkoutPlanDay day = createDayFromRequest(dayRequest);
                plan.addDay(day);
            }
        }

        WorkoutPlan saved = workoutPlanRepo.save(plan);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deletePlan(Long id, String username) {
        User user = findUserOrThrow(username);
        WorkoutPlan plan = workoutPlanRepo.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Workout plan not found"));

        // Clear FK references in workout_log before deleting (to avoid constraint violation)
        List<Long> dayIds = plan.getDays().stream()
                .map(WorkoutPlanDay::getId)
                .filter(dayId -> dayId != null)
                .toList();
        if (!dayIds.isEmpty()) {
            workoutLogRepo.clearWorkoutPlanDayReferences(dayIds);
        }

        workoutPlanRepo.delete(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutPlanDayResponse getPlanDayById(Long dayId, String username) {
        User user = findUserOrThrow(username);
        WorkoutPlanDay day = workoutPlanDayRepo.findByIdAndWorkoutPlanUserId(dayId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Workout plan day not found"));
        return toDayResponse(day);
    }

    @Override
    public List<PredefinedExerciseResponse> getPredefinedExercises(ExerciseCategory category) {
        return Arrays.stream(PredefinedExercise.values())
                .filter(e -> category == null || e.getCategory() == category)
                .map(e -> PredefinedExerciseResponse.builder()
                        .id(e.name())
                        .name(e.getDisplayName())
                        .category(e.getCategory())
                        .build())
                .toList();
    }

    @Override
    public List<ExerciseCategory> getCategories() {
        return Arrays.asList(ExerciseCategory.values());
    }

    private WorkoutPlanDay createDayFromRequest(WorkoutPlanDayRequest request) {
        WorkoutPlanDay day = WorkoutPlanDay.builder()
                .dayNumber(request.getDayNumber())
                .dayName(request.getDayName())
                .exercises(new LinkedHashSet<>())
                .build();

        if (request.getExercises() != null) {
            for (WorkoutPlanExerciseRequest exerciseRequest : request.getExercises()) {
                WorkoutPlanExercise exercise = WorkoutPlanExercise.builder()
                        .name(exerciseRequest.getName())
                        .category(exerciseRequest.getCategory())
                        .isCustom(exerciseRequest.getIsCustom() != null ? exerciseRequest.getIsCustom() : false)
                        .targetSets(exerciseRequest.getTargetSets())
                        .targetReps(exerciseRequest.getTargetReps())
                        .targetWeight(exerciseRequest.getTargetWeight())
                        .build();
                day.getExercises().add(exercise);
            }
        }

        return day;
    }

    private WorkoutPlanResponse toResponse(WorkoutPlan plan) {
        List<WorkoutPlanDayResponse> days = plan.getDays().stream()
                .sorted(Comparator.comparing(WorkoutPlanDay::getDayNumber))
                .map(this::toDayResponse)
                .toList();

        return WorkoutPlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .createdAt(plan.getCreatedAt())
                .days(days)
                .build();
    }

    private WorkoutPlanDayResponse toDayResponse(WorkoutPlanDay day) {
        List<WorkoutPlanExerciseResponse> exercises = day.getExercises().stream()
                .map(e -> WorkoutPlanExerciseResponse.builder()
                        .name(e.getName())
                        .category(e.getCategory())
                        .isCustom(e.getIsCustom())
                        .targetSets(e.getTargetSets())
                        .targetReps(e.getTargetReps())
                        .targetWeight(e.getTargetWeight())
                        .build())
                .toList();

        return WorkoutPlanDayResponse.builder()
                .id(day.getId())
                .dayNumber(day.getDayNumber())
                .dayName(day.getDayName())
                .exercises(exercises)
                .build();
    }
}
