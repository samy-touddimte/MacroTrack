package com.macrotrack.analytics.controller;

import com.macrotrack.analytics.tdee.TdeeResponse;
import com.macrotrack.analytics.projection.TrendResponse;

import com.macrotrack.auth.security.CurrentUserEmail;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.PastOrPresent;
import com.macrotrack.analytics.projection.WeightTrendService;
import com.macrotrack.analytics.tdee.TdeeEstimationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.macrotrack.validation.DateValidationService;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics", description = "Dashboard and metabolic analytics")
@RequiredArgsConstructor
@Validated
public class AnalyticsController {

    private final TdeeEstimationService tdeeEstimationService;
    private final WeightTrendService weightTrendService;
    private final DateValidationService dateValidationService;

    @GetMapping("/weight-trend")
    @Operation(summary = "Compute weight trend (EWMA smoothing)")
    public TrendResponse getWeightTrend(@CurrentUserEmail String email,
                                         @PastOrPresent @RequestParam("from") LocalDate from,
                                         @RequestParam("to") LocalDate to) {
        dateValidationService.validateDateRange(from, to);
        return weightTrendService.getWeightTrend(email, from, to);
    }

    @GetMapping("/tdee")
    @Operation(summary = "Compute dynamic TDEE")
    public TdeeResponse getTdee(@CurrentUserEmail String email,
                                 @PastOrPresent @RequestParam("from") LocalDate from,
                                 @RequestParam("to") LocalDate to) {
        dateValidationService.validateDateRange(from, to);
        return tdeeEstimationService.getTdee(email, from, to);
    }
}
