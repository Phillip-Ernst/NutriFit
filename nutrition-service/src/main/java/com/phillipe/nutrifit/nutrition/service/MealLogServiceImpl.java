package com.phillipe.nutrifit.nutrition.service;

import com.phillipe.nutrifit.nutrition.dto.request.FoodItemRequest;
import com.phillipe.nutrifit.nutrition.dto.request.MealLogRequest;
import com.phillipe.nutrifit.nutrition.dto.response.FoodItemResponse;
import com.phillipe.nutrifit.nutrition.dto.response.MealLogResponse;
import com.phillipe.nutrifit.nutrition.model.embedded.MealFoodEntry;
import com.phillipe.nutrifit.nutrition.model.entity.MealLog;
import com.phillipe.nutrifit.nutrition.repository.MealLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MealLogServiceImpl implements MealLogService {

    private final MealLogRepository mealLogRepo;

    public MealLogServiceImpl(MealLogRepository mealLogRepo) {
        this.mealLogRepo = mealLogRepo;
    }

    private int nz(Integer v) { return v == null ? 0 : v; }

    @Override
    @Transactional
    public MealLogResponse createMeal(MealLogRequest request, String username) {
        MealLog meal = MealLog.builder()
                .username(username)
                .build();

        int totalCals = 0, totalP = 0, totalCarbs = 0, totalFats = 0;

        for (FoodItemRequest food : request.getFoods()) {
            MealFoodEntry entry = new MealFoodEntry(
                    food.getType(),
                    food.getCalories(),
                    food.getProtein(),
                    food.getCarbs(),
                    food.getFats()
            );
            meal.getFoods().add(entry);

            totalCals += nz(food.getCalories());
            totalP += nz(food.getProtein());
            totalCarbs += nz(food.getCarbs());
            totalFats += nz(food.getFats());
        }

        meal.setTotalCalories(totalCals);
        meal.setTotalProtein(totalP);
        meal.setTotalCarbs(totalCarbs);
        meal.setTotalFats(totalFats);

        MealLog saved = mealLogRepo.save(meal);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MealLogResponse> getMyMeals(String username) {
        return mealLogRepo.findByUsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteMeal(Long id, String username) {
        MealLog meal = mealLogRepo.findByIdAndUsername(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Meal not found or access denied"));
        mealLogRepo.delete(meal);
    }

    private MealLogResponse toResponse(MealLog meal) {
        return MealLogResponse.builder()
                .id(meal.getId())
                .createdAt(meal.getCreatedAt())
                .totalCalories(meal.getTotalCalories())
                .totalProtein(meal.getTotalProtein())
                .totalCarbs(meal.getTotalCarbs())
                .totalFats(meal.getTotalFats())
                .foods(meal.getFoods().stream()
                        .map(FoodItemResponse::fromEmbedded)
                        .toList())
                .build();
    }
}