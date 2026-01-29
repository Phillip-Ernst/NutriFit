package com.phillipe.NutriFit.WorkoutPlan.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutPlanResponse {
    private Long id;
    private String name;
    private String description;
    private Instant createdAt;
    private List<WorkoutPlanDayResponse> days;
}
