package com.phillipe.NutriFit.WorkoutLog.dto.response;

import com.phillipe.NutriFit.WorkoutLog.dto.request.ExerciseItemRequest;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutLogResponse {
    private Long id;
    private Instant createdAt;

    private Integer totalDurationMinutes;
    private Integer totalCaloriesBurned;
    private Integer totalSets;
    private Integer totalReps;

    private List<ExerciseItemRequest> exercises;
}
