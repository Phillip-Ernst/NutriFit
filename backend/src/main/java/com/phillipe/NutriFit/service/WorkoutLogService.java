package com.phillipe.NutriFit.service;

import com.phillipe.NutriFit.dto.request.WorkoutLogRequest;
import com.phillipe.NutriFit.dto.response.WorkoutLogResponse;
import com.phillipe.NutriFit.dto.request.WorkoutLogFromPlanRequest;

import java.util.List;

public interface WorkoutLogService {
    WorkoutLogResponse createWorkout(WorkoutLogRequest request, String username);
    WorkoutLogResponse createWorkoutFromPlan(WorkoutLogFromPlanRequest request, String username);
    List<WorkoutLogResponse> getMyWorkouts(String username);
    void deleteWorkout(Long id, String username);
}
