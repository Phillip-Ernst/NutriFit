package com.phillipe.nutrifit.nutrition.controller;

import com.phillipe.nutrifit.nutrition.dto.request.MealLogRequest;
import com.phillipe.nutrifit.nutrition.dto.response.MealLogResponse;
import com.phillipe.nutrifit.nutrition.service.MealLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
public class MealLogController {

    private final MealLogService mealLogService;

    @PostMapping
    public MealLogResponse createMeal(@Valid @RequestBody MealLogRequest request,
                                      Authentication authentication) {
        String username = authentication.getName();
        return mealLogService.createMeal(request, username);
    }

    @GetMapping("/mine")
    public List<MealLogResponse> myMeals(Authentication authentication) {
        String username = authentication.getName();
        return mealLogService.getMyMeals(username);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        mealLogService.deleteMeal(id, username);
        return ResponseEntity.noContent().build();
    }
}