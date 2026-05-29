package com.macrotrack.dashboard.service;

import com.macrotrack.analytics.projection.MultiScenarioProjectionResponse;
import com.macrotrack.analytics.projection.ProjectionResponse;

public interface WeightProjectionService {
    ProjectionResponse getProjection(String email);
    MultiScenarioProjectionResponse getMultiScenarioProjection(String email);
}
