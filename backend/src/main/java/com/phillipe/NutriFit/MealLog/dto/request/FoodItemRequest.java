package com.phillipe.NutriFit.MealLog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItemRequest {

    @NotBlank(message = "type is required")
    private String type;

    private Integer calories;

    private Integer protein;

    private Integer carbs;

    private Integer fats;
}
