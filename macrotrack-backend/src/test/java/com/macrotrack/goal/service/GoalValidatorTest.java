package com.macrotrack.goal.service;

import com.macrotrack.shared.exception.GoalValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class GoalValidatorTest {

    @Test
    @DisplayName("lossGoal_targetAboveCurrent_throwsException")
    void lossGoal_targetAboveCurrent_throwsException() {
        assertThrows(GoalValidationException.class, () -> GoalValidator.validateGoal(80.0, 90.0, -0.5));
    }

    @Test
    @DisplayName("gainGoal_targetBelowCurrent_throwsException")
    void gainGoal_targetBelowCurrent_throwsException() {
        assertThrows(GoalValidationException.class, () -> GoalValidator.validateGoal(80.0, 70.0, 0.5));
    }

    @Test
    @DisplayName("lossGoal_targetBelowCurrent_noException")
    void lossGoal_targetBelowCurrent_noException() {
        assertDoesNotThrow(() -> GoalValidator.validateGoal(80.0, 70.0, -0.5));
    }

    @Test
    @DisplayName("gainGoal_targetAboveCurrent_noException")
    void gainGoal_targetAboveCurrent_noException() {
        assertDoesNotThrow(() -> GoalValidator.validateGoal(80.0, 90.0, 0.5));
    }

    @Test
    @DisplayName("maintainGoal_differentWeights_throwsException")
    void maintainGoal_differentWeights_throwsException() {
        assertThrows(GoalValidationException.class, () -> GoalValidator.validateGoal(80.0, 70.0, 0.0));
    }

    @Test
    @DisplayName("nullInputs_noException")
    void nullInputs_noException() {
        assertDoesNotThrow(() -> GoalValidator.validateGoal(null, null, null));
    }
}
