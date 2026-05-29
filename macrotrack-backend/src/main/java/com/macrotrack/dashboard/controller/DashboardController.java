package com.macrotrack.dashboard.controller;

import com.macrotrack.goal.model.Goal;

import com.macrotrack.analytics.projection.MetabolicForecastResponse;
import com.macrotrack.analytics.projection.MultiScenarioProjectionResponse;
import com.macrotrack.analytics.projection.ProjectionResponse;
import com.macrotrack.dashboard.dto.DashboardResponse;

import com.macrotrack.auth.security.CurrentUserEmail;
import com.macrotrack.dashboard.service.DashboardAssembler;
import com.macrotrack.dashboard.service.WeightProjectionService;
import com.macrotrack.dashboard.service.MetabolicForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.validation.annotation.Validated;
import com.macrotrack.validation.DateValidationService;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Aggregated data for main dashboard view")
@RequiredArgsConstructor
@Validated
public class DashboardController {

    private final DashboardAssembler dashboardAssembler;
    private final WeightProjectionService weightProjectionService;
    private final MetabolicForecastService metabolicForecastService;
    private final DateValidationService dateValidationService;

    @GetMapping
    @Operation(summary = "Get main dashboard data")
    public DashboardResponse getDashboard(@CurrentUserEmail String email, @PastOrPresent @RequestParam(value = "date", required = false) LocalDate date) {
        if (date != null) {
            dateValidationService.validatePastDateLimit(date);
        }
        return dashboardAssembler.assembleDashboard(email, date);
    }

    @GetMapping("/projection")
    @Operation(summary = "Generate weight and goal projections")
    public ProjectionResponse getProjection(@CurrentUserEmail String email) {
        return weightProjectionService.getProjection(email);
    }

    @GetMapping("/multi-scenario-projection")
    @Operation(summary = "Generate multi-scenario projections")
    public MultiScenarioProjectionResponse getMultiScenarioProjection(@CurrentUserEmail String email) {
        return weightProjectionService.getMultiScenarioProjection(email);
    }

    @GetMapping("/metabolic-forecast")
    @Operation(summary = "Simulate a custom metabolic forecast")
    public MetabolicForecastResponse getMetabolicForecast(
            @CurrentUserEmail String email,
            @RequestParam @Positive(message = "Current weight must be positive") double currentWeight,
            @RequestParam @Positive(message = "Goal weight must be positive") double goalWeight,
            @RequestParam @DecimalMin(value = "-1.5", message = "Weekly rate cannot be lower than -1.5") @DecimalMax(value = "1.5", message = "Weekly rate cannot be higher than +1.5") double weeklyRate,
            @PastOrPresent @RequestParam LocalDate startDate
    ) {
        return metabolicForecastService.getMetabolicForecast(
                email, currentWeight, goalWeight, weeklyRate, startDate);
    }
}
