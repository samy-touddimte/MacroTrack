package com.macrotrack.nutrition.service;

import com.macrotrack.nutrition.dto.MacroTargets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class MacroCalculatorServiceTest {

    private MacroCalculatorService macroCalculatorService;

    @BeforeEach
    void setUp() {
        macroCalculatorService = new MacroCalculatorService();
    }

    @Test
    @DisplayName("normalBudget_proteinsFirstThenFatThenCarbs")
    void normalBudget_proteinsFirstThenFatThenCarbs() {
        MacroTargets targets = macroCalculatorService.computeMacroTargets(80.0, null, 2000.0, -0.5);
        assertNotNull(targets);
        assertTrue(targets.proteinG() > targets.fatG());
    }

    @Test
    @DisplayName("insufficientBudget_fallsBackToMinimums")
    void insufficientBudget_fallsBackToMinimums() {
        MacroTargets targets = macroCalculatorService.computeMacroTargets(70.0, null, 800.0, -0.5);
        assertTrue(targets.carbsG() >= 130.0);
        assertTrue(targets.fatG() >= 30.0);
    }

    @Test
    @DisplayName("maintainGoal_usesMaintenanceProtein")
    void maintainGoal_usesMaintenanceProtein() {
        MacroTargets targets = macroCalculatorService.computeMacroTargets(70.0, null, 2000.0, 0.0);
        assertEquals(70.0 * 2.0, targets.proteinG(), 0.1);
    }

    @Test
    @DisplayName("withBodyFat_usesLBMForProtein")
    void withBodyFat_usesLBMForProtein() {
        MacroTargets targets = macroCalculatorService.computeMacroTargets(100.0, 20.0, 2000.0, -0.5);
        assertEquals(80.0 * 2.2, targets.proteinG(), 0.1);
    }

    @Test
    @DisplayName("withoutBodyFat_usesTotalWeightForProtein")
    void withoutBodyFat_usesTotalWeightForProtein() {
        MacroTargets targets = macroCalculatorService.computeMacroTargets(100.0, null, 2000.0, -0.5);
        assertEquals(100.0 * 2.2, targets.proteinG(), 0.1);
    }
}
