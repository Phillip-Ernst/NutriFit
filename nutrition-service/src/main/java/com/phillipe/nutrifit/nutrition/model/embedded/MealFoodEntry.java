package com.phillipe.nutrifit.nutrition.model.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
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