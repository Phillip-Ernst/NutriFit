package com.phillipe.NutriFit.controller;

import com.phillipe.NutriFit.dto.response.PredefinedExerciseResponse;
import com.phillipe.NutriFit.model.ExerciseCategory;
import com.phillipe.NutriFit.service.WorkoutPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final WorkoutPlanService workoutPlanService;

    @GetMapping("/predefined")
    public List<PredefinedExerciseResponse> getPredefinedExercises(
            @RequestParam(required = false) ExerciseCategory category) {
        return workoutPlanService.getPredefinedExercises(category);
    }

    @GetMapping("/categories")
    public List<ExerciseCategory> getCategories() {
        return workoutPlanService.getCategories();
    }
}
