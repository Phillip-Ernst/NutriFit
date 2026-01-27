package com.phillipe.NutriFit.MealLog.service;

import com.phillipe.NutriFit.MealLog.dao.MealLogRepo;
import com.phillipe.NutriFit.MealLog.dto.request.FoodItemRequest;
import com.phillipe.NutriFit.MealLog.dto.request.MealLogRequest;
import com.phillipe.NutriFit.MealLog.dto.response.MealLogResponse;
import com.phillipe.NutriFit.MealLog.model.MealFoodEntry;
import com.phillipe.NutriFit.MealLog.model.MealLog;
import com.phillipe.NutriFit.User.dao.UserRepo;
import com.phillipe.NutriFit.User.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MealLogServiceImpl implements MealLogService {

    private final MealLogRepo mealLogRepo;
    private final UserRepo userRepo;

    public MealLogServiceImpl(MealLogRepo mealLogRepo, UserRepo userRepo) {
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
        MealLogResponse resp = new MealLogResponse();
        resp.setId(meal.getId());
        resp.setCreatedAt(meal.getCreatedAt());
        resp.setTotalCalories(meal.getTotalCalories());
        resp.setTotalProtein(meal.getTotalProtein());
        resp.setTotalCarbs(meal.getTotalCarbs());
        resp.setTotalFats(meal.getTotalFats());

        // reuse FoodItemRequest as a simple response shape (fine for now)
        List<FoodItemRequest> foods = meal.getFoods().stream().map(e -> {
            FoodItemRequest f = new FoodItemRequest();
            f.setType(e.getType());
            f.setCalories(e.getCalories());
            f.setProtein(e.getProtein());
            f.setCarbs(e.getCarbs());
            f.setFats(e.getFats());
            return f;
        }).toList();

        resp.setFoods(foods);
        return resp;
    }
}
