package com.phillipe.NutriFit.WorkoutPlan.service;

import com.phillipe.NutriFit.WorkoutPlan.dto.request.WorkoutPlanRequest;
import com.phillipe.NutriFit.WorkoutPlan.dto.response.PredefinedExerciseResponse;
import com.phillipe.NutriFit.WorkoutPlan.dto.response.WorkoutPlanDayResponse;
import com.phillipe.NutriFit.WorkoutPlan.dto.response.WorkoutPlanResponse;
import com.phillipe.NutriFit.WorkoutPlan.model.ExerciseCategory;

import java.util.List;

public interface WorkoutPlanService {

    WorkoutPlanResponse createPlan(WorkoutPlanRequest request, String username);

    List<WorkoutPlanResponse> getMyPlans(String username);

    WorkoutPlanResponse getPlanById(Long id, String username);

    WorkoutPlanResponse updatePlan(Long id, WorkoutPlanRequest request, String username);

    void deletePlan(Long id, String username);

    WorkoutPlanDayResponse getPlanDayById(Long dayId, String username);

    List<PredefinedExerciseResponse> getPredefinedExercises(ExerciseCategory category);

    List<ExerciseCategory> getCategories();
}
