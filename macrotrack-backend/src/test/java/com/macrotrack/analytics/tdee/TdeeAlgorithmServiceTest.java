package com.macrotrack.analytics.tdee;

import com.macrotrack.analytics.common.DateValuePoint;
import com.macrotrack.user.model.ActivityLevel;
import com.macrotrack.user.model.BiologicalSex;
import com.macrotrack.user.model.User;
import com.macrotrack.analytics.projection.WeightSmoothingService;
import com.macrotrack.analytics.bmr.BmrCalculatorService;
import com.macrotrack.analytics.bmr.MifflinStJeorFormula;
import com.macrotrack.analytics.bmr.KatchMcArdleFormula;

import com.macrotrack.analytics.tdee.TdeeAlgorithmService;
import com.macrotrack.shared.util.MetabolicConstants;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TdeeAlgorithmServiceTest {

    private static final double DELTA = 0.01;

    private TdeeAlgorithmService tdeeAlgorithmService;
    private WeightSmoothingService weightSmoothingService;
    private BmrCalculatorService bmrCalculatorService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        bmrCalculatorService = new BmrCalculatorService(List.of(
                new KatchMcArdleFormula(),
                new MifflinStJeorFormula(fixedClock)
        ));
        tdeeAlgorithmService = new TdeeAlgorithmService(bmrCalculatorService);
        weightSmoothingService = new WeightSmoothingService();
    }

    @Test
    void ewmaSmoothesNoise() {
        Map<LocalDate, Double> rawData = new LinkedHashMap<>();
        rawData.put(LocalDate.of(2026, 1, 1), 80.0);
        rawData.put(LocalDate.of(2026, 1, 2), 82.0);
        rawData.put(LocalDate.of(2026, 1, 3), 79.0);
        rawData.put(LocalDate.of(2026, 1, 4), 81.0);

        Map<LocalDate, Double> trend = weightSmoothingService.computeWeightTrend(rawData);

        assertEquals(4, trend.size());
        List<Double> values = new ArrayList<>(trend.values());
        assertTrue(Math.abs(values.get(1) - values.get(0)) < Math.abs(82.0 - 80.0));
        assertTrue(Math.abs(values.get(2) - values.get(1)) < Math.abs(79.0 - 82.0));
        assertTrue(Math.abs(values.get(3) - values.get(2)) < Math.abs(81.0 - 79.0));
    }

    @Test
    void initialTdee_sedentaryMale() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        var user = new User();
        user.setSex(BiologicalSex.MALE);
        user.setBirthDate(LocalDate.now(fixedClock).minusYears(25));
        user.setHeightCm(180.0);
        user.setActivityLevel(ActivityLevel.SEDENTARY);

        Double tdee = bmrCalculatorService.computeInitialTdee(user, 80.0, null);

        assertNotNull(tdee);
        assertTrue(tdee > 2150 && tdee < 2250, "Expected TDEE between 2150 and 2250 but was " + tdee);
    }



    @Test
    void estimatedTdee_insufficientData_returnsEmpty() {
        Map<LocalDate, Double> shortTrend = new LinkedHashMap<>();
        shortTrend.put(LocalDate.of(2026, 1, 1), 80.0);
        shortTrend.put(LocalDate.of(2026, 1, 2), 79.8);

        Map<LocalDate, Double> calories = new LinkedHashMap<>();
        calories.put(LocalDate.of(2026, 1, 1), 2000.0);
        calories.put(LocalDate.of(2026, 1, 2), 2100.0);

        Map<LocalDate, Double> result = tdeeAlgorithmService.computeEstimatedTdee(new User(), shortTrend, calories, null);

        assertTrue(result.isEmpty(), "Less than 5 days of data should return empty TDEE estimate");
    }

    @Test
    void initialTdee_incompleteProfile_returnsDefault() {
        var user = new User();
        user.setSex(BiologicalSex.MALE);
        user.setHeightCm(180.0);

        Double tdee = bmrCalculatorService.computeInitialTdee(user, 80.0, null);

        assertNotNull(tdee);
        assertEquals(MetabolicConstants.DEFAULT_TDEE_KCAL, tdee, DELTA);
    }
}
