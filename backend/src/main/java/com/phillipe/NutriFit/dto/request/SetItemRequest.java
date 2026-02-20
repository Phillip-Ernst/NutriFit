package com.phillipe.NutriFit.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetItemRequest {

    @NotNull(message = "set number is required")
    @Min(value = 1, message = "set number must be at least 1")
    private Integer setNumber;

    @Min(value = 0, message = "reps must be non-negative")
    private Integer reps;

    @Min(value = 0, message = "weight must be non-negative")
    private Integer weight;

    @Builder.Default
    private Boolean completed = true;

    private String notes;
}
