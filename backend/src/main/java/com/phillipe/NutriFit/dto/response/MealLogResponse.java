package com.phillipe.NutriFit.dto.response;

import com.phillipe.NutriFit.dto.request.FoodItemRequest;
import lombok.*;

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

    private List<FoodItemRequest> foods;
}
