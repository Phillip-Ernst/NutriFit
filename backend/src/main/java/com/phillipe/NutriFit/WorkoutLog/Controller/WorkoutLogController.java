package com.phillipe.NutriFit.WorkoutLog.controller;

import com.phillipe.NutriFit.WorkoutLog.dto.request.WorkoutLogRequest;
import com.phillipe.NutriFit.WorkoutLog.dto.response.WorkoutLogResponse;
import com.phillipe.NutriFit.WorkoutLog.service.WorkoutLogService;
import com.phillipe.NutriFit.WorkoutPlan.dto.request.WorkoutLogFromPlanRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workouts")
@RequiredArgsConstructor
public class WorkoutLogController {

    private final WorkoutLogService workoutLogService;

    @PostMapping
    public WorkoutLogResponse createWorkout(@Valid @RequestBody WorkoutLogRequest request,
                                            Authentication authentication) {
        String username = authentication.getName();
        return workoutLogService.createWorkout(request, username);
    }

    @PostMapping("/from-plan")
    @ResponseStatus(HttpStatus.CREATED)
    public WorkoutLogResponse createWorkoutFromPlan(@Valid @RequestBody WorkoutLogFromPlanRequest request,
                                                     Authentication authentication) {
        String username = authentication.getName();
        return workoutLogService.createWorkoutFromPlan(request, username);
    }

    @GetMapping("/mine")
    public List<WorkoutLogResponse> myWorkouts(Authentication authentication) {
        String username = authentication.getName();
        return workoutLogService.getMyWorkouts(username);
    }
}
