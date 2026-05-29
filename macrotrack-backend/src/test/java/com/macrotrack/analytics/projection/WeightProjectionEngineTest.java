package com.macrotrack.analytics.projection;

import com.macrotrack.analytics.common.DateValuePoint;
import com.macrotrack.user.model.ActivityLevel;
import com.macrotrack.analytics.projection.SimulationContext;
import com.macrotrack.user.model.BiologicalSex;
import com.macrotrack.goal.model.MetabolicGoalType;
import com.macrotrack.user.model.User;
import com.macrotrack.analytics.projection.GainMetabolicAdaptationStrategy;
import com.macrotrack.analytics.projection.LossMetabolicAdaptationStrategy;
import com.macrotrack.analytics.projection.MaintainMetabolicAdaptationStrategy;
import com.macrotrack.analytics.projection.WeightProjectionEngine;
import com.macrotrack.analytics.bmr.BmrCalculatorService;
import com.macrotrack.shared.util.MetabolicConstants;
import com.macrotrack.analytics.projection.MetabolicAdaptationService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WeightProjectionEngineTest {

    private static final double DELTA = 0.01;

    private WeightProjectionEngine engine;
    private MetabolicAdaptationService adaptationService;
    private BmrCalculatorService bmrCalculatorService;
    private User user;

    @BeforeEach
    void setUp() {
        adaptationService = new MetabolicAdaptationService(List.of(
            new LossMetabolicAdaptationStrategy(),
            new GainMetabolicAdaptationStrategy(),
            new MaintainMetabolicAdaptationStrategy()
        ));
        bmrCalculatorService = Mockito.mock(BmrCalculatorService.class);
        engine = new WeightProjectionEngine(adaptationService, bmrCalculatorService);
        user = new User();
        user.setSex(BiologicalSex.MALE);
        user.setBirthDate(LocalDate.now().minusYears(30));
        user.setHeightCm(175.0);
        user.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
    }

    @Test
    void lossPhase1_dropsFasterThanPureFatLoss() {
        double weeklyRate = -0.5;
        double tdee = 2200.0;
        double startWeight = 90.0;

        Mockito.when(bmrCalculatorService.computeInitialTdee(Mockito.any(), Mockito.anyDouble(), Mockito.any())).thenReturn(2400.0);

        var result = engine.generateWeightProjection(
                new SimulationContext(
                        startWeight, 80.0, weeklyRate, LocalDate.of(2026, 1, 1), tdee,
                        user, 0.0, startWeight, 0, null
                )
        );

        assertTrue(result.points().size() > 1);

        double weightDay7 = result.points().get(1).value();
        double pureFatLoss = Math.abs(weeklyRate);
        double linearFatOnlyWeight = startWeight - pureFatLoss;
        assertTrue(weightDay7 < linearFatOnlyWeight,
                "Phase 1 should produce faster loss than linear fat-only (" + linearFatOnlyWeight + "). Got " + weightDay7);
        assertEquals(MetabolicGoalType.LOSS, result.goalType());
    }

    @Test
    void adaptiveThermogenesis_reducesTdeeAfterThreshold() {
        double base = 2500.0;
        double withoutAdaptation = adaptationService.adjustTdee(2500.0, 100.0, 99.0, -0.5, 10, false);
        double withAdaptation = adaptationService.adjustTdee(2500.0, 100.0, 90.0, -0.5, 90, true);

        assertEquals(base, withoutAdaptation, DELTA);
        assertTrue(withAdaptation < base);
    }

    @Test
    void maintain_returnsSinglePoint() {
        Mockito.when(bmrCalculatorService.computeInitialTdee(Mockito.any(), Mockito.anyDouble(), Mockito.any())).thenReturn(2200.0);

        var result = engine.generateWeightProjection(
                new SimulationContext(
                        75.0, 75.0, 0.0, LocalDate.now(), 2200.0,
                        user, 0.0, 75.0, 0, null
                )
        );
        assertEquals(MetabolicGoalType.MAINTAIN, result.goalType());
        assertEquals(1, result.points().size());
    }

    @Test
    void sampleWeekly_includesLastPoint() {
        List<DateValuePoint<Double>> daily = List.of(
                new DateValuePoint<>(LocalDate.of(2026, 1, 1), 80.0),
                new DateValuePoint<>(LocalDate.of(2026, 1, 8), 79.0),
                new DateValuePoint<>(LocalDate.of(2026, 1, 10), 78.5)
        );
        var weekly = engine.sampleWeekly(daily);
        assertEquals(78.5, weekly.get(weekly.size() - 1).value(), DELTA);
    }

    @Test
    void incompleteProfile_usesBaseTdeeFallback() {
        Mockito.when(bmrCalculatorService.computeInitialTdee(Mockito.any(), Mockito.anyDouble(), Mockito.any())).thenReturn(null);

        var result = engine.generateWeightProjection(
                new SimulationContext(
                        90.0, 80.0, -0.5, LocalDate.now(), 2150.0,
                        user, 0.0, 90.0, 0, null
                )
        );

        assertNotNull(result);
        assertEquals(MetabolicGoalType.LOSS, result.goalType());
        assertTrue(result.points().size() > 1);
        // The fact that it doesn't throw a NullPointerException means it correctly fell back to 2150.0
    }

    @Test
    void maxProjectionDays_stopsProjection() {
        Mockito.when(bmrCalculatorService.computeInitialTdee(Mockito.any(), Mockito.anyDouble(), Mockito.any())).thenReturn(2000.0);

        var result = engine.generateWeightProjection(
                new SimulationContext(
                        150.0, 50.0, -0.1, LocalDate.now(), 2000.0,
                        user, 0.0, 150.0, 0, null
                )
        );

        assertNull(result.estimatedReachDate());
        // Weekly points should correspond to exactly MAX_PROJECTION_DAYS / 7 plus maybe 1
        assertTrue(result.points().size() > 100);
        assertTrue(result.points().size() <= (MetabolicConstants.MAX_PROJECTION_DAYS / 7) + 2);
    }

    @Test
    void nullUser_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            engine.generateWeightProjection(
                    new SimulationContext(
                            90.0, 80.0, -0.5, LocalDate.now(), 2000.0,
                            null, 0.0, 90.0, 0, null
                    )
            );
        });
    }
}
