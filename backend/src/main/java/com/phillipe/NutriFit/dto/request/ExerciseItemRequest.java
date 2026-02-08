package com.phillipe.NutriFit.dto.request;

import jakarta.validation.constraints.Min;
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

    @Min(value = 0, message = "duration must be non-negative")
    private Integer durationMinutes;

    @Min(value = 0, message = "sets must be non-negative")
    private Integer sets;

    @Min(value = 0, message = "reps must be non-negative")
    private Integer reps;

    @Min(value = 0, message = "weight must be non-negative")
    private Integer weight;

    @Min(value = 0, message = "calories burned must be non-negative")
    private Integer caloriesBurned;
}
