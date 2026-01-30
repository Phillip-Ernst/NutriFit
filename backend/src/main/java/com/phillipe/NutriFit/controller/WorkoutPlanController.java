package com.phillipe.NutriFit.controller;

import com.phillipe.NutriFit.dto.request.WorkoutPlanRequest;
import com.phillipe.NutriFit.dto.response.WorkoutPlanDayResponse;
import com.phillipe.NutriFit.dto.response.WorkoutPlanResponse;
import com.phillipe.NutriFit.service.WorkoutPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workout-plans")
@RequiredArgsConstructor
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkoutPlanResponse createPlan(@Valid @RequestBody WorkoutPlanRequest request,
                                          Authentication authentication) {
        String username = authentication.getName();
        return workoutPlanService.createPlan(request, username);
    }

    @GetMapping("/mine")
    public List<WorkoutPlanResponse> getMyPlans(Authentication authentication) {
        String username = authentication.getName();
        return workoutPlanService.getMyPlans(username);
    }

    @GetMapping("/{id}")
    public WorkoutPlanResponse getPlanById(@PathVariable Long id,
                                           Authentication authentication) {
        String username = authentication.getName();
        return workoutPlanService.getPlanById(id, username);
    }

    @PutMapping("/{id}")
    public WorkoutPlanResponse updatePlan(@PathVariable Long id,
                                          @Valid @RequestBody WorkoutPlanRequest request,
                                          Authentication authentication) {
        String username = authentication.getName();
        return workoutPlanService.updatePlan(id, request, username);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlan(@PathVariable Long id,
                           Authentication authentication) {
        String username = authentication.getName();
        workoutPlanService.deletePlan(id, username);
    }

    @GetMapping("/days/{dayId}")
    public WorkoutPlanDayResponse getPlanDayById(@PathVariable Long dayId,
                                                  Authentication authentication) {
        String username = authentication.getName();
        return workoutPlanService.getPlanDayById(dayId, username);
    }
}
