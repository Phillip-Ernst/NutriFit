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
@Table(name = "body_measurement")
public class BodyMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @Column(nullable = false)
    private Instant recordedAt = Instant.now();

    // All stored in metric (kg, cm)
    private Double heightCm;
    private Double weightKg;
    private Double bodyFatPercent;

    // Upper body
    private Double neckCm;
    private Double shouldersCm;
    private Double chestCm;
    private Double bicepsCm;
    private Double forearmsCm;

    // Core
    private Double waistCm;
    private Double hipsCm;

    // Lower body
    private Double thighsCm;
    private Double calvesCm;

    private String notes;
}
