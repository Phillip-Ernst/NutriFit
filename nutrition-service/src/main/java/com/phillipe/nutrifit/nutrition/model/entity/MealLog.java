package com.phillipe.nutrifit.nutrition.model.entity;

import com.phillipe.nutrifit.nutrition.model.embedded.MealFoodEntry;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "meal_log")
public class MealLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Builder.Default
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Builder.Default
    @Column(nullable = false)
    private Integer totalCalories = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer totalProtein = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer totalCarbs = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer totalFats = 0;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "meal_log_foods", joinColumns = @JoinColumn(name = "meal_log_id"))
    private List<MealFoodEntry> foods = new ArrayList<>();
}