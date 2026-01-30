package com.phillipe.NutriFit.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealLogRequest {

    @NotEmpty(message = "foods list cannot be empty")
    private List<@Valid FoodItemRequest> foods;
}
