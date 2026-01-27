package com.phillipe.NutriFit.User.service.impl;

import com.phillipe.NutriFit.MealLog.dao.MealLogRepo;
import com.phillipe.NutriFit.MealLog.dto.request.FoodItemRequest;
import com.phillipe.NutriFit.MealLog.dto.request.MealLogRequest;
import com.phillipe.NutriFit.MealLog.dto.response.MealLogResponse;
import com.phillipe.NutriFit.User.model.MealFoodEntry;
import com.phillipe.NutriFit.User.model.MealLog;
import com.phillipe.NutriFit.User.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MealLogServiceImpl implements MealLogService {

    private final MealLogRepository mealLogRepository;
    private final UserService userService;

    public MealLogServiceImpl(MealLogRepo mealLogRepository, UserService userService) {
        this.mealLogRepository = mealLogRepository;
        this.userService = userService;
    }

    private int nz(Integer v) { return v == null ? 0 : v; }

    @Override
    @Transactional
    public MealLogResponse createMeal(MealLogRequest request, String username) {
        User user = userService.findByUsername(username); // implement/you likely already have

        MealLog meal = new MealLog();
        meal.setUser(user);

        int totalCals = 0, totalP = 0, totalCarbs = 0, totalFats = 0;

        for (FoodItemRequest item : request.getFoods()) {
            // type is validated as required by @NotBlank, but you can double-safeguard if you want
            MealFoodEntry entry = new MealFoodEntry(
                    item.getType(),
                    item.getCalories(),
                    item.getProtein(),
                    item.getCarbs(),
                    item.getFats()
            );
            meal.getFoods().add(entry);

            totalCals += nz(item.getCalories());
            totalP += nz(item.getProtein());
            totalCarbs += nz(item.getCarbs());
            totalFats += nz(item.getFats());
        }

        meal.setTotalCalories(totalCals);
        meal.setTotalProtein(totalP);
        meal.setTotalCarbs(totalCarbs);
        meal.setTotalFats(totalFats);

        MealLog saved = mealLogRepository.save(meal);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MealLogResponse> getMyMeals(String username) {
        User user = userService.findByUsername(username);
        return mealLogRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private MealLogResponse toResponse(MealLog meal) {
        MealLogResponse r = new MealLogResponse();
        r.setId(meal.getId());
        r.setCreatedAt(meal.getCreatedAt());
        r.setTotalCalories(meal.getTotalCalories());
        r.setTotalProtein(meal.getTotalProtein());
        r.setTotalCarbs(meal.getTotalCarbs());
        r.setTotalFats(meal.getTotalFats());

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

        r.setFoods(foods);
        return r;
    }
}
