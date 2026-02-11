package com.phillipe.NutriFit.service.impl;

import com.phillipe.NutriFit.repository.MealLogRepository;
import com.phillipe.NutriFit.dto.request.FoodItemRequest;
import com.phillipe.NutriFit.dto.request.MealLogRequest;
import com.phillipe.NutriFit.dto.response.FoodItemResponse;
import com.phillipe.NutriFit.dto.response.MealLogResponse;
import com.phillipe.NutriFit.model.embedded.MealFoodEntry;
import com.phillipe.NutriFit.model.entity.MealLog;
import com.phillipe.NutriFit.repository.UserRepository;
import com.phillipe.NutriFit.model.entity.User;
import com.phillipe.NutriFit.service.MealLogService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MealLogServiceImpl implements MealLogService {

    private final MealLogRepository mealLogRepo;
    private final UserRepository userRepo;

    public MealLogServiceImpl(MealLogRepository mealLogRepo, UserRepository userRepo) {
        this.mealLogRepo = mealLogRepo;
        this.userRepo = userRepo;
    }

    private int nz(Integer v) { return v == null ? 0 : v; }

    @Override
    @Transactional
    public MealLogResponse createMeal(MealLogRequest request, String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }

        MealLog meal = MealLog.builder()
                .user(user)
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
        User user = userRepo.findByUsername(username);
        return mealLogRepo.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
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
