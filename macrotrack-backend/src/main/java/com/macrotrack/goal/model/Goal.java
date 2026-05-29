package com.macrotrack.goal.model;

import com.macrotrack.user.model.User;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "target_weight_kg", nullable = false)
    private Double targetWeightKg;

    @Column(name = "weekly_rate_kg", nullable = false)
    private Double weeklyRateKg;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean active = true;
}
