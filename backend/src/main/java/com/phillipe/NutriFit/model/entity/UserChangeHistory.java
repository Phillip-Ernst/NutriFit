package com.phillipe.NutriFit.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_change_history")
public class UserChangeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String entityType;  // "PROFILE" or "MEASUREMENT"

    private Long entityId;      // null for profile, measurement ID for measurements

    @Column(nullable = false, length = 50)
    private String fieldName;   // e.g., "birthYear", "weightKg"

    @Column(columnDefinition = "TEXT")
    private String oldValue;    // null if first set

    @Column(columnDefinition = "TEXT")
    private String newValue;

    @Builder.Default
    @Column(nullable = false)
    private Instant changedAt = Instant.now();
}
