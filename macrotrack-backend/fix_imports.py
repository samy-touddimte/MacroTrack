import os

files_to_fix = [
    "src/main/java/com/nutritrack/analytics/tdee/TdeeResponse.java",
    "src/main/java/com/nutritrack/analytics/projection/MetabolicForecastResponse.java",
    "src/main/java/com/nutritrack/analytics/projection/TrendResponse.java",
    "src/main/java/com/nutritrack/analytics/projection/ProjectionResponse.java",
    "src/main/java/com/nutritrack/analytics/projection/MultiScenarioProjectionResponse.java"
]

import_statement = "import com.nutritrack.analytics.common.DateValuePoint;\n"

for f in files_to_fix:
    if not os.path.exists(f):
        continue
    with open(f, "r") as file:
        content = file.read()
    
    if "import com.nutritrack.analytics.common.DateValuePoint;" not in content:
        new_content = content.replace(";\n", ";\n" + import_statement, 1)
        with open(f, "w") as file:
            file.write(new_content)
