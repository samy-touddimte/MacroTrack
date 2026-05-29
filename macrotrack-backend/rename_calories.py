import os
import re

def replace_in_file(path, old, new):
    with open(path, 'r') as f:
        content = f.read()
    content = content.replace(old, new)
    with open(path, 'w') as f:
        f.write(content)

base_dir = "/home/samy/nutritrack/macrotrack-backend/src/main/java/com/nutritrack/"

# 1. FoodLogRequest
replace_in_file(base_dir + "dto/nutrition/FoodLogRequest.java", "Double calories,", "Double caloriesKcal,")
replace_in_file(base_dir + "service/nutrition/FoodLogServiceImpl.java", "request.calories()", "request.caloriesKcal()")

# 2. FoodLogResponse
replace_in_file(base_dir + "dto/nutrition/FoodLogResponse.java", "Double calories,", "Double caloriesKcal,")

# 3. DailyNutritionResponse
replace_in_file(base_dir + "dto/nutrition/DailyNutritionResponse.java", "double calories,", "double caloriesKcal,")
replace_in_file(base_dir + "service/dashboard/DashboardAssembler.java", "todayNutrition.calories()", "todayNutrition.caloriesKcal()")

# 4. DailyCalorieAggregation
replace_in_file(base_dir + "dto/analytics/DailyCalorieAggregation.java", "Double calories", "Double caloriesKcal")
replace_in_file(base_dir + "service/nutrition/NutritionAnalyticsServiceImpl.java", "agg.calories()", "agg.caloriesKcal()")

# 5. DashboardResponse
replace_in_file(base_dir + "dto/dashboard/DashboardResponse.java", "Double todayCalories,", "Double todayCaloriesKcal,")
replace_in_file(base_dir + "service/dashboard/DashboardAssembler.java", "todayCalories(todayNutrition.calories())", "todayCaloriesKcal(todayNutrition.caloriesKcal())")

# 6. TdeeResponse
replace_in_file(base_dir + "dto/analytics/TdeeResponse.java", "caloriesConsumed", "caloriesConsumedKcal")
replace_in_file(base_dir + "service/analytics/tdee/TdeeEstimationServiceImpl.java", "calPoints, currentTdee", "calPoints, currentTdee") # calPoints are mapped to caloriesConsumed
replace_in_file(base_dir + "service/analytics/tdee/TdeeEstimationServiceImpl.java", "TdeeResponse(tdeePoints, calPoints, currentTdee)", "TdeeResponse(tdeePoints, calPoints, currentTdee)")

print("Done")
