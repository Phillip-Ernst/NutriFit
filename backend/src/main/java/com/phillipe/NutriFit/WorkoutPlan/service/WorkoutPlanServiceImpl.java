package com.phillipe.NutriFit.WorkoutPlan.service;

import com.phillipe.NutriFit.User.dao.UserRepo;
import com.phillipe.NutriFit.User.model.User;
import com.phillipe.NutriFit.WorkoutPlan.dao.WorkoutPlanDayRepo;
import com.phillipe.NutriFit.WorkoutPlan.dao.WorkoutPlanRepo;
import com.phillipe.NutriFit.WorkoutPlan.dto.request.WorkoutPlanDayRequest;
import com.phillipe.NutriFit.WorkoutPlan.dto.request.WorkoutPlanExerciseRequest;
import com.phillipe.NutriFit.WorkoutPlan.dto.request.WorkoutPlanRequest;
import com.phillipe.NutriFit.WorkoutPlan.dto.response.PredefinedExerciseResponse;
import com.phillipe.NutriFit.WorkoutPlan.dto.response.WorkoutPlanDayResponse;
import com.phillipe.NutriFit.WorkoutPlan.dto.response.WorkoutPlanExerciseResponse;
import com.phillipe.NutriFit.WorkoutPlan.dto.response.WorkoutPlanResponse;
import com.phillipe.NutriFit.WorkoutPlan.model.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class WorkoutPlanServiceImpl implements WorkoutPlanService {

    private final WorkoutPlanRepo workoutPlanRepo;
    private final WorkoutPlanDayRepo workoutPlanDayRepo;
    private final UserRepo userRepo;

    public WorkoutPlanServiceImpl(WorkoutPlanRepo workoutPlanRepo,
                                   WorkoutPlanDayRepo workoutPlanDayRepo,
                                   UserRepo userRepo) {
        this.workoutPlanRepo = workoutPlanRepo;
        this.workoutPlanDayRepo = workoutPlanDayRepo;
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
                .exercises(new ArrayList<>())
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
