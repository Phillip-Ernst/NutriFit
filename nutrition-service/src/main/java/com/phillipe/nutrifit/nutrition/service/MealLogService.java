package com.phillipe.nutrifit.nutrition.service;

import com.phillipe.nutrifit.nutrition.dto.request.MealLogRequest;
import com.phillipe.nutrifit.nutrition.dto.response.MealLogResponse;

import java.util.List;

public interface MealLogService {
    MealLogResponse createMeal(MealLogRequest request, String username);
    List<MealLogResponse> getMyMeals(String username);
    void deleteMeal(Long id, String username);
}