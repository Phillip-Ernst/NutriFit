package com.phillipe.NutriFit.WorkoutLog.service;

import com.phillipe.NutriFit.WorkoutLog.dto.request.WorkoutLogRequest;
import com.phillipe.NutriFit.WorkoutLog.dto.response.WorkoutLogResponse;

import java.util.List;

public interface WorkoutLogService {
    WorkoutLogResponse createWorkout(WorkoutLogRequest request, String username);
    List<WorkoutLogResponse> getMyWorkouts(String username);
}
