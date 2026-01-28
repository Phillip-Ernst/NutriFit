package com.phillipe.NutriFit.MealLog.model;

import com.phillipe.NutriFit.User.model.User;
import jakarta.persistence.*;
import lombok.*;

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

    // ties meal to the logged-in user
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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
