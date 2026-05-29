import os
import re

replacements = {
    "com.macrotrack.service.analytics.BmrFormula": "com.macrotrack.service.analytics.formula.BmrFormula",
    "com.macrotrack.service.analytics.KatchMcArdleFormula": "com.macrotrack.service.analytics.formula.KatchMcArdleFormula",
    "com.macrotrack.service.analytics.MifflinStJeorFormula": "com.macrotrack.service.analytics.formula.MifflinStJeorFormula",
    "com.macrotrack.service.analytics.PhaseBonusStrategy": "com.macrotrack.service.analytics.strategy.PhaseBonusStrategy",
    "com.macrotrack.service.analytics.GainPhaseBonusStrategy": "com.macrotrack.service.analytics.strategy.GainPhaseBonusStrategy",
    "com.macrotrack.service.analytics.LossPhaseBonusStrategy": "com.macrotrack.service.analytics.strategy.LossPhaseBonusStrategy",
    "com.macrotrack.service.analytics.MaintainPhaseBonusStrategy": "com.macrotrack.service.analytics.strategy.MaintainPhaseBonusStrategy",
    "com.macrotrack.service.analytics.BmrCalculatorService": "com.macrotrack.service.analytics.estimation.BmrCalculatorService",
    "com.macrotrack.service.analytics.TdeeEstimationService": "com.macrotrack.service.analytics.estimation.TdeeEstimationService",
    "com.macrotrack.service.analytics.TdeeAlgorithmService": "com.macrotrack.service.analytics.estimation.TdeeAlgorithmService",
    "com.macrotrack.service.analytics.DataConfidenceService": "com.macrotrack.service.analytics.estimation.DataConfidenceService",
    "com.macrotrack.service.analytics.MetabolicEngineService": "com.macrotrack.service.analytics.forecast.MetabolicEngineService",
    "com.macrotrack.service.analytics.WeightSmoothingService": "com.macrotrack.service.analytics.forecast.WeightSmoothingService",
    "com.macrotrack.service.analytics.WeightTrendService": "com.macrotrack.service.analytics.forecast.WeightTrendService",
    "com.macrotrack.dto.request.LoginRequest": "com.macrotrack.dto.auth.LoginRequest",
    "com.macrotrack.dto.request.RegisterRequest": "com.macrotrack.dto.auth.RegisterRequest",
    "com.macrotrack.dto.request.RefreshRequest": "com.macrotrack.dto.auth.RefreshRequest",
    "com.macrotrack.dto.response.AuthResponse": "com.macrotrack.dto.auth.AuthResponse",
    "com.macrotrack.dto.request.UpdateUserRequest": "com.macrotrack.dto.user.UpdateUserRequest",
    "com.macrotrack.dto.response.UserResponse": "com.macrotrack.dto.user.UserResponse",
    "com.macrotrack.dto.request.FoodLogRequest": "com.macrotrack.dto.nutrition.FoodLogRequest",
    "com.macrotrack.dto.response.FoodLogResponse": "com.macrotrack.dto.nutrition.FoodLogResponse",
    "com.macrotrack.dto.response.DailyNutritionResponse": "com.macrotrack.dto.nutrition.DailyNutritionResponse",
    "com.macrotrack.dto.response.MacroTargets": "com.macrotrack.dto.nutrition.MacroTargets",
    "com.macrotrack.dto.request.GoalRequest": "com.macrotrack.dto.goal.GoalRequest",
    "com.macrotrack.dto.response.GoalResponse": "com.macrotrack.dto.goal.GoalResponse",
    "com.macrotrack.dto.request.WeightEntryRequest": "com.macrotrack.dto.weight.WeightEntryRequest",
    "com.macrotrack.dto.response.WeightEntryResponse": "com.macrotrack.dto.weight.WeightEntryResponse",
    "com.macrotrack.dto.response.DashboardResponse": "com.macrotrack.dto.dashboard.DashboardResponse",
    "com.macrotrack.dto.response.MetabolicForecastResponse": "com.macrotrack.dto.analytics.MetabolicForecastResponse",
    "com.macrotrack.dto.response.ProjectionResponse": "com.macrotrack.dto.analytics.ProjectionResponse",
    "com.macrotrack.dto.response.MultiScenarioProjectionResponse": "com.macrotrack.dto.analytics.MultiScenarioProjectionResponse",
    "com.macrotrack.dto.response.TrendResponse": "com.macrotrack.dto.analytics.TrendResponse",
    "com.macrotrack.dto.response.TdeeResponse": "com.macrotrack.dto.analytics.TdeeResponse",
    "com.macrotrack.dto.response.AdherenceMetrics": "com.macrotrack.dto.analytics.AdherenceMetrics",
    "com.macrotrack.dto.response.DateValuePoint": "com.macrotrack.dto.analytics.DateValuePoint",
    "com.macrotrack.dto.response.ErrorResponse": "com.macrotrack.dto.common.ErrorResponse"
}

def process_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()
    
    modified = False
    for old, new in replacements.items():
        if old in content:
            # handle 'import ' prefix just to be safe, but actually class names might be used fully qualified
            # we'll just replace the exact string globally
            content = content.replace(old, new)
            modified = True
            
    # also handle wildcards if any
    content = content.replace("import com.macrotrack.dto.request.*;", "import com.macrotrack.dto.auth.*;\nimport com.macrotrack.dto.user.*;\nimport com.macrotrack.dto.nutrition.*;\nimport com.macrotrack.dto.goal.*;\nimport com.macrotrack.dto.weight.*;")
    content = content.replace("import com.macrotrack.dto.response.*;", "import com.macrotrack.dto.auth.*;\nimport com.macrotrack.dto.user.*;\nimport com.macrotrack.dto.nutrition.*;\nimport com.macrotrack.dto.goal.*;\nimport com.macrotrack.dto.weight.*;\nimport com.macrotrack.dto.dashboard.*;\nimport com.macrotrack.dto.analytics.*;\nimport com.macrotrack.dto.common.*;")
    
    if modified:
        with open(filepath, 'w') as f:
            f.write(content)

for root, _, files in os.walk('src/main/java'):
    for file in files:
        if file.endswith('.java'):
            process_file(os.path.join(root, file))

for root, _, files in os.walk('src/test/java'):
    for file in files:
        if file.endswith('.java'):
            process_file(os.path.join(root, file))
