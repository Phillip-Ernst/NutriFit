package com.phillipe.NutriFit.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutPlanRequest {

    @NotBlank(message = "plan name is required")
    private String name;

    private String description;

    @NotEmpty(message = "plan must have at least one day")
    private List<@Valid WorkoutPlanDayRequest> days;
}
