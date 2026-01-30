package com.phillipe.NutriFit.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseItemRequest {

    @NotBlank(message = "name is required")
    private String name;

    private String category;

    private Integer durationMinutes;

    private Integer sets;

    private Integer reps;

    private Integer weight;

    private Integer caloriesBurned;
}
