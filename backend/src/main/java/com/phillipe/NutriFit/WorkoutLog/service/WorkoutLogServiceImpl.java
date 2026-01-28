package com.phillipe.NutriFit.WorkoutLog.service;

import com.phillipe.NutriFit.User.dao.UserRepo;
import com.phillipe.NutriFit.User.model.User;
import com.phillipe.NutriFit.WorkoutLog.dao.WorkoutLogRepo;
import com.phillipe.NutriFit.WorkoutLog.dto.request.ExerciseItemRequest;
import com.phillipe.NutriFit.WorkoutLog.dto.request.WorkoutLogRequest;
import com.phillipe.NutriFit.WorkoutLog.dto.response.WorkoutLogResponse;
import com.phillipe.NutriFit.WorkoutLog.model.WorkoutExerciseEntry;
import com.phillipe.NutriFit.WorkoutLog.model.WorkoutLog;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkoutLogServiceImpl implements WorkoutLogService {

    private final WorkoutLogRepo workoutLogRepo;
    private final UserRepo userRepo;

    public WorkoutLogServiceImpl(WorkoutLogRepo workoutLogRepo, UserRepo userRepo) {
        this.workoutLogRepo = workoutLogRepo;
        this.userRepo = userRepo;
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

        int totalDuration = 0, totalCalories = 0, totalSets = 0, totalReps = 0;

        for (ExerciseItemRequest exercise : request.getExercises()) {
            WorkoutExerciseEntry entry = new WorkoutExerciseEntry(
                    exercise.getName(),
                    exercise.getCategory(),
                    exercise.getDurationMinutes(),
                    exercise.getSets(),
                    exercise.getReps(),
                    exercise.getWeight(),
                    exercise.getCaloriesBurned()
            );
            workout.getExercises().add(entry);

            totalDuration += nz(exercise.getDurationMinutes());
            totalCalories += nz(exercise.getCaloriesBurned());
            totalSets += nz(exercise.getSets());
            totalReps += nz(exercise.getReps());
        }

        workout.setTotalDurationMinutes(totalDuration);
        workout.setTotalCaloriesBurned(totalCalories);
        workout.setTotalSets(totalSets);
        workout.setTotalReps(totalReps);

        WorkoutLog saved = workoutLogRepo.save(workout);
        return toResponse(saved);
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

    private WorkoutLogResponse toResponse(WorkoutLog workout) {
        WorkoutLogResponse resp = new WorkoutLogResponse();
        resp.setId(workout.getId());
        resp.setCreatedAt(workout.getCreatedAt());
        resp.setTotalDurationMinutes(workout.getTotalDurationMinutes());
        resp.setTotalCaloriesBurned(workout.getTotalCaloriesBurned());
        resp.setTotalSets(workout.getTotalSets());
        resp.setTotalReps(workout.getTotalReps());

        List<ExerciseItemRequest> exercises = workout.getExercises().stream().map(e -> {
            ExerciseItemRequest ex = new ExerciseItemRequest();
            ex.setName(e.getName());
            ex.setCategory(e.getCategory());
            ex.setDurationMinutes(e.getDurationMinutes());
            ex.setSets(e.getSets());
            ex.setReps(e.getReps());
            ex.setWeight(e.getWeight());
            ex.setCaloriesBurned(e.getCaloriesBurned());
            return ex;
        }).toList();

        resp.setExercises(exercises);
        return resp;
    }
}
