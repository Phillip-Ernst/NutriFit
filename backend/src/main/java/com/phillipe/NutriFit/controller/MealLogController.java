package com.phillipe.NutriFit.controller;

import com.phillipe.NutriFit.dto.request.MealLogRequest;
import com.phillipe.NutriFit.dto.response.MealLogResponse;
import com.phillipe.NutriFit.service.MealLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
public class MealLogController {

    private final MealLogService mealLogService;

    // Create a new meal log (one meal with multiple foods)
    @PostMapping
    public MealLogResponse createMeal(@Valid @RequestBody MealLogRequest request,
                                      Authentication authentication) {

        String username = authentication.getName();
        return mealLogService.createMeal(request, username);
    }

    // Get all meals for the logged-in user
    @GetMapping("/mine")
    public List<MealLogResponse> myMeals(Authentication authentication) {

        String username = authentication.getName();
        return mealLogService.getMyMeals(username);
    }

    // Delete a meal by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        mealLogService.deleteMeal(id, username);
        return ResponseEntity.noContent().build();
    }
}
