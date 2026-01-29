package com.phillipe.NutriFit.WorkoutPlan.dto.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutPlanDayResponse {
    private Long id;
    private Integer dayNumber;
    private String dayName;
    private List<WorkoutPlanExerciseResponse> exercises;
}
