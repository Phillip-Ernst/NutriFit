package com.phillipe.NutriFit.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phillipe.NutriFit.repository.UserRepository;
import com.phillipe.NutriFit.model.entity.User;
import com.phillipe.NutriFit.repository.WorkoutLogRepository;
import com.phillipe.NutriFit.dto.request.ExerciseItemRequest;
import com.phillipe.NutriFit.dto.request.SetItemRequest;
import com.phillipe.NutriFit.dto.request.WorkoutLogRequest;
import com.phillipe.NutriFit.dto.response.WorkoutLogResponse;
import com.phillipe.NutriFit.model.embedded.WorkoutExerciseEntry;
import com.phillipe.NutriFit.model.entity.WorkoutLog;
import com.phillipe.NutriFit.repository.WorkoutPlanDayRepository;
import com.phillipe.NutriFit.dto.request.WorkoutLogFromPlanRequest;
import com.phillipe.NutriFit.model.entity.WorkoutPlanDay;
import com.phillipe.NutriFit.service.WorkoutLogService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorkoutLogServiceImpl implements WorkoutLogService {

    private final WorkoutLogRepository workoutLogRepo;
    private final WorkoutPlanDayRepository workoutPlanDayRepo;
    private final UserRepository userRepo;
    private final ObjectMapper objectMapper;

    public WorkoutLogServiceImpl(WorkoutLogRepository workoutLogRepo,
                                  WorkoutPlanDayRepository workoutPlanDayRepo,
                                  UserRepository userRepo,
                                  ObjectMapper objectMapper) {
        this.workoutLogRepo = workoutLogRepo;
        this.workoutPlanDayRepo = workoutPlanDayRepo;
        this.userRepo = userRepo;
        this.objectMapper = objectMapper;
    }

    private int nz(Integer v) { return v == null ? 0 : v; }

    @Override
    @Transactional
    public WorkoutLogResponse createWorkout(WorkoutLogRequest request, String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }

        WorkoutLog workout = WorkoutLog.builder()
                .user(user)
                .build();

        populateWorkoutFromExercises(workout, request.getExercises());

        WorkoutLog saved = workoutLogRepo.save(workout);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public WorkoutLogResponse createWorkoutFromPlan(WorkoutLogFromPlanRequest request, String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }

        WorkoutPlanDay planDay = workoutPlanDayRepo.findByIdAndWorkoutPlanUserId(
                request.getWorkoutPlanDayId(), user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Workout plan day not found"));

        WorkoutLog workout = WorkoutLog.builder()
                .user(user)
                .workoutPlanDay(planDay)
                .build();

        populateWorkoutFromExercises(workout, request.getExercises());

        WorkoutLog saved = workoutLogRepo.save(workout);
        return toResponse(saved);
    }

    private void populateWorkoutFromExercises(WorkoutLog workout, List<ExerciseItemRequest> exercises) {
        int totalDuration = 0, totalCalories = 0, totalSets = 0, totalReps = 0;

        for (ExerciseItemRequest exercise : exercises) {
            List<SetItemRequest> setDetails = exercise.getSetDetails();
            String setDetailsJson = null;

            if (setDetails != null && !setDetails.isEmpty()) {
                try {
                    setDetailsJson = objectMapper.writeValueAsString(setDetails);
                } catch (JsonProcessingException e) {
                    throw new IllegalArgumentException("Failed to serialize set details", e);
                }

                // Calculate totals from setDetails
                totalSets += setDetails.size();
                totalReps += setDetails.stream()
                        .mapToInt(s -> nz(s.getReps()))
                        .sum();
            } else {
                // Use scalar values
                totalSets += nz(exercise.getSets());
                totalReps += nz(exercise.getReps());
            }

            WorkoutExerciseEntry entry = WorkoutExerciseEntry.builder()
                    .name(exercise.getName())
                    .category(exercise.getCategory())
                    .durationMinutes(exercise.getDurationMinutes())
                    .sets(exercise.getSets())
                    .reps(exercise.getReps())
                    .weight(exercise.getWeight())
                    .caloriesBurned(exercise.getCaloriesBurned())
                    .setDetailsJson(setDetailsJson)
                    .build();

            workout.getExercises().add(entry);

            totalDuration += nz(exercise.getDurationMinutes());
            totalCalories += nz(exercise.getCaloriesBurned());
        }

        workout.setTotalDurationMinutes(totalDuration);
        workout.setTotalCaloriesBurned(totalCalories);
        workout.setTotalSets(totalSets);
        workout.setTotalReps(totalReps);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutLogResponse> getMyWorkouts(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }
        return workoutLogRepo.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteWorkout(Long id, String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }
        WorkoutLog workout = workoutLogRepo.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Workout not found or access denied"));
        workoutLogRepo.delete(workout);
    }

    private WorkoutLogResponse toResponse(WorkoutLog workout) {
        WorkoutLogResponse resp = new WorkoutLogResponse();
        resp.setId(workout.getId());
        resp.setCreatedAt(workout.getCreatedAt());
        resp.setTotalDurationMinutes(workout.getTotalDurationMinutes());
        resp.setTotalCaloriesBurned(workout.getTotalCaloriesBurned());
        resp.setTotalSets(workout.getTotalSets());
        resp.setTotalReps(workout.getTotalReps());

        WorkoutPlanDay planDay = workout.getWorkoutPlanDay();
        if (planDay != null) {
            resp.setWorkoutPlanDayId(planDay.getId());
            resp.setWorkoutPlanDayName(planDay.getDayName());
        }

        List<ExerciseItemRequest> exercises = workout.getExercises().stream().map(e -> {
            ExerciseItemRequest ex = new ExerciseItemRequest();
            ex.setName(e.getName());
            ex.setCategory(e.getCategory());
            ex.setDurationMinutes(e.getDurationMinutes());
            ex.setSets(e.getSets());
            ex.setReps(e.getReps());
            ex.setWeight(e.getWeight());
            ex.setCaloriesBurned(e.getCaloriesBurned());

            // Deserialize setDetails if present
            if (e.getSetDetailsJson() != null && !e.getSetDetailsJson().isBlank()) {
                try {
                    List<SetItemRequest> setDetails = objectMapper.readValue(
                            e.getSetDetailsJson(),
                            new TypeReference<List<SetItemRequest>>() {}
                    );
                    ex.setSetDetails(setDetails);
                } catch (JsonProcessingException ignored) {
                    // If deserialization fails, leave setDetails null
                }
            }

            return ex;
        }).toList();

        resp.setExercises(exercises);
        return resp;
    }
}
