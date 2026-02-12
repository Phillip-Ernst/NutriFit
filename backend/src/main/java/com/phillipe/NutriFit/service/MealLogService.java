package com.phillipe.NutriFit.service;

import com.phillipe.NutriFit.dto.request.MealLogRequest;
import com.phillipe.NutriFit.dto.response.MealLogResponse;

import java.util.List;

public interface MealLogService {
    MealLogResponse createMeal(MealLogRequest request, String username);
    List<MealLogResponse> getMyMeals(String username);
    void deleteMeal(Long id, String username);
}
