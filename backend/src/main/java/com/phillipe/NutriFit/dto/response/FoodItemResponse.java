package com.phillipe.NutriFit.dto.response;

import com.phillipe.NutriFit.model.embedded.MealFoodEntry;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItemResponse {

    private String type;
    private Integer calories;
    private Integer protein;
    private Integer carbs;
    private Integer fats;

    public static FoodItemResponse fromEmbedded(MealFoodEntry entry) {
        return FoodItemResponse.builder()
                .type(entry.getType())
                .calories(entry.getCalories())
                .protein(entry.getProtein())
                .carbs(entry.getCarbs())
                .fats(entry.getFats())
                .build();
    }
}
