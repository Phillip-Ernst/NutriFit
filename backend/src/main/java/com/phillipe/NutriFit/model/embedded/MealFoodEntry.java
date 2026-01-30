package com.phillipe.NutriFit.model.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealFoodEntry {

    @Column(nullable = false)
    private String type;

    private Integer calories;

    private Integer protein;

    private Integer carbs;

    private Integer fats;
}
