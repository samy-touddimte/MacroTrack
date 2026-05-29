import os
import glob
import shutil

mapping = {
    "AdherenceMetrics.java": "com.nutritrack.analytics.adherence",
    "DailyCalorieAggregation.java": "com.nutritrack.nutrition.dto",
    "DateValuePoint.java": "com.nutritrack.analytics.common",
    "MetabolicForecastResponse.java": "com.nutritrack.analytics.projection",
    "MultiScenarioProjectionResponse.java": "com.nutritrack.analytics.projection",
    "ProjectionResponse.java": "com.nutritrack.analytics.projection",
    "TdeeResponse.java": "com.nutritrack.analytics.tdee",
    "TrendResponse.java": "com.nutritrack.analytics.projection",
    "TrendVariations.java": "com.nutritrack.analytics.projection",
    "WeightForecastResult.java": "com.nutritrack.analytics.projection"
}

base_dir = "src/main/java"
old_dto_dir = os.path.join(base_dir, "com/nutritrack/analytics/dto")

# 1. Move files and update their package declarations
for filename, new_pkg in mapping.items():
    old_path = os.path.join(old_dto_dir, filename)
    new_dir = os.path.join(base_dir, new_pkg.replace(".", "/"))
    os.makedirs(new_dir, exist_ok=True)
    new_path = os.path.join(new_dir, filename)
    
    if os.path.exists(old_path):
        with open(old_path, "r") as f:
            content = f.read()
            
        content = content.replace("package com.nutritrack.analytics.dto;", f"package {new_pkg};")
        
        with open(new_path, "w") as f:
            f.write(content)
            
        os.remove(old_path)

# 2. Delete the old dto directory if empty
try:
    os.rmdir(old_dto_dir)
except OSError:
    pass

# 3. Update all imports in src/main/java and src/test/java
java_files = glob.glob("src/main/java/**/*.java", recursive=True) + glob.glob("src/test/java/**/*.java", recursive=True)

for filepath in java_files:
    if not os.path.isfile(filepath):
        continue
    with open(filepath, "r") as f:
        content = f.read()
        
    original_content = content
    for filename, new_pkg in mapping.items():
        class_name = filename.replace(".java", "")
        old_import = f"import com.nutritrack.analytics.dto.{class_name};"
        new_import = f"import {new_pkg}.{class_name};"
        content = content.replace(old_import, new_import)
        
        # Also replace fully qualified names just in case
        old_fqn = f"com.nutritrack.analytics.dto.{class_name}"
        new_fqn = f"{new_pkg}.{class_name}"
        content = content.replace(old_fqn, new_fqn)
        
    if content != original_content:
        with open(filepath, "w") as f:
            f.write(content)
