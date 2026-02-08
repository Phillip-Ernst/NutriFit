package com.phillipe.NutriFit.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItemRequest {

    @NotBlank(message = "type is required")
    private String type;

    @Min(value = 0, message = "calories must be non-negative")
    private Integer calories;

    @Min(value = 0, message = "protein must be non-negative")
    private Integer protein;

    @Min(value = 0, message = "carbs must be non-negative")
    private Integer carbs;

    @Min(value = 0, message = "fats must be non-negative")
    private Integer fats;
}
