package com.phillipe.nutrifit.nutrition.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealLogResponse {
    private Long id;
    private Instant createdAt;
    private Integer totalCalories;
    private Integer totalProtein;
    private Integer totalCarbs;
    private Integer totalFats;
    private List<FoodItemResponse> foods;
}