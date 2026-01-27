package com.phillipe.NutriFit.User.service;

import com.phillipe.NutriFit.MealLog.dto.request.MealLogRequest;
import com.phillipe.NutriFit.MealLog.dto.response.MealLogResponse;

import java.util.List;

public interface MealLogService {
    MealLogResponse createMeal(MealLogRequest request, String username);
    List<MealLogResponse> getMyMeals(String username);
}
