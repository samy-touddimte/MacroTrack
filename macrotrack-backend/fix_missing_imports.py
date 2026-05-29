import os
import re

BASE_DIR = "src/main/java/com/nutritrack"

# These classes were previously in a flat package (model or mapper) and are now split.
# Mapping: ClassName -> New FQCN
classes_to_check = {
    # Models
    "User": "com.nutritrack.user.model.User",
    "ActivityLevel": "com.nutritrack.user.model.ActivityLevel",
    "BiologicalSex": "com.nutritrack.user.model.BiologicalSex",
    "TrainingType": "com.nutritrack.user.model.TrainingType",
    "TrainingExperience": "com.nutritrack.user.model.TrainingExperience",
    "WeightEntry": "com.nutritrack.weight.model.WeightEntry",
    "FoodLog": "com.nutritrack.nutrition.model.FoodLog",
    "Goal": "com.nutritrack.goal.model.Goal",
    "MetabolicGoalType": "com.nutritrack.goal.model.MetabolicGoalType",
    "RefreshToken": "com.nutritrack.auth.model.RefreshToken",
    
    # Mappers
    "UserMapper": "com.nutritrack.user.mapper.UserMapper",
    "WeightEntryMapper": "com.nutritrack.weight.mapper.WeightEntryMapper",
    "FoodLogMapper": "com.nutritrack.nutrition.mapper.FoodLogMapper",
    "GoalMapper": "com.nutritrack.goal.mapper.GoalMapper",
    "DateMapper": "com.nutritrack.shared.mapper.DateMapper"
}

def main():
    for root, dirs, files in os.walk(BASE_DIR):
        for file in files:
            if not file.endswith(".java"):
                continue
            
            filepath = os.path.join(root, file)
            with open(filepath, "r", encoding="utf-8") as f:
                content = f.read()
                
            # Determine the current package
            package_match = re.search(r"^package\s+([a-zA-Z0-9_.]+);", content, re.MULTILINE)
            if not package_match:
                continue
            
            current_package = package_match.group(1)
            modified = False
            
            for class_name, fqcn in classes_to_check.items():
                target_package = fqcn.rsplit(".", 1)[0]
                
                # If the file is in the same package as the class, no import needed
                if current_package == target_package:
                    continue
                
                # If the class name is used as a whole word in the file
                # We need to make sure it's not part of another word or a string
                # Regex \bClassName\b works well. 
                if re.search(rf"\b{class_name}\b", content):
                    # Check if the import already exists
                    import_stmt = f"import {fqcn};"
                    if import_stmt not in content:
                        # Insert the import statement right after the package declaration
                        # Add a blank line for cleanliness
                        replacement = f"package {current_package};\n\n{import_stmt}"
                        content = content.replace(f"package {current_package};", replacement, 1)
                        modified = True
                        print(f"Added '{import_stmt}' to {filepath}")
                        
            if modified:
                with open(filepath, "w", encoding="utf-8") as f:
                    f.write(content)

if __name__ == "__main__":
    main()
