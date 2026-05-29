import os
import re
import shutil

BASE_DIR = "src/main/java/com/nutritrack"

# Mapping: filename -> new sub-package
file_mapping = {
    # Auth
    "AuthController.java": "auth/controller",
    "AuthService.java": "auth/service",
    "AuthServiceImpl.java": "auth/service",
    "RefreshTokenService.java": "auth/service",
    "TokenCleanupService.java": "auth/service",
    "UserDetailsServiceImpl.java": "auth/service",
    "LoginRequest.java": "auth/dto",
    "RegisterRequest.java": "auth/dto",
    "AuthResponse.java": "auth/dto",
    "RefreshRequest.java": "auth/dto",
    "ChangePasswordRequest.java": "auth/dto",
    "RefreshToken.java": "auth/model",
    "RefreshTokenRepository.java": "auth/repository",
    "JwtUtils.java": "auth/security",
    "JwtAuthFilter.java": "auth/security",
    "RateLimitingFilter.java": "auth/security",
    "CurrentUserEmail.java": "auth/security",
    "CurrentUserEmailArgumentResolver.java": "auth/security",
    "CustomAuthenticationEntryPoint.java": "auth/security",

    # User
    "UserController.java": "user/controller",
    "UserService.java": "user/service",
    "UserServiceImpl.java": "user/service",
    "UserInternalQueryPort.java": "user/service",
    "UpdateUserRequest.java": "user/dto",
    "UserResponse.java": "user/dto",
    "User.java": "user/model",
    "ActivityLevel.java": "user/model",
    "BiologicalSex.java": "user/model",
    "TrainingType.java": "user/model",
    "TrainingExperience.java": "user/model",
    "UserRepository.java": "user/repository",
    "UserMapper.java": "user/mapper",

    # Weight
    "WeightEntryController.java": "weight/controller",
    "WeightEntryService.java": "weight/service",
    "WeightEntryServiceImpl.java": "weight/service",
    "WeightEntryRequest.java": "weight/dto",
    "WeightEntryResponse.java": "weight/dto",
    "WeightEntry.java": "weight/model",
    "WeightEntryRepository.java": "weight/repository",
    "WeightEntryMapper.java": "weight/mapper",

    # Nutrition
    "FoodLogController.java": "nutrition/controller",
    "FoodLogService.java": "nutrition/service",
    "FoodLogServiceImpl.java": "nutrition/service",
    "NutritionAnalyticsService.java": "nutrition/service",
    "NutritionAnalyticsServiceImpl.java": "nutrition/service",
    "MacroCalculatorService.java": "nutrition/service",
    "FoodLogRequest.java": "nutrition/dto",
    "FoodLogResponse.java": "nutrition/dto",
    "DailyNutritionResponse.java": "nutrition/dto",
    "MacroTargets.java": "nutrition/dto",
    "FoodLog.java": "nutrition/model",
    "FoodLogRepository.java": "nutrition/repository",
    "FoodLogMapper.java": "nutrition/mapper",

    # Goal
    "GoalController.java": "goal/controller",
    "GoalService.java": "goal/service",
    "GoalServiceImpl.java": "goal/service",
    "GoalInternalQueryPort.java": "goal/service",
    "GoalValidator.java": "goal/service",
    "GoalRequest.java": "goal/dto",
    "GoalResponse.java": "goal/dto",
    "Goal.java": "goal/model",
    "MetabolicGoalType.java": "goal/model",
    "GoalRepository.java": "goal/repository",
    "GoalMapper.java": "goal/mapper",

    # Analytics
    "AnalyticsController.java": "analytics/controller",
    "AdherenceService.java": "analytics/adherence",
    "DataConfidenceService.java": "analytics/adherence",
    "ConfidenceTier.java": "analytics/adherence",
    "BmrCalculatorService.java": "analytics/bmr",
    "BmrFormula.java": "analytics/bmr",
    "KatchMcArdleFormula.java": "analytics/bmr",
    "MifflinStJeorFormula.java": "analytics/bmr",
    "WeightProjectionEngine.java": "analytics/projection",
    "WeightSmoothingService.java": "analytics/projection",
    "WeightTrendService.java": "analytics/projection",
    "MetabolicAdaptationService.java": "analytics/projection",
    "MetabolicAdaptationStrategy.java": "analytics/projection",
    "LossMetabolicAdaptationStrategy.java": "analytics/projection",
    "GainMetabolicAdaptationStrategy.java": "analytics/projection",
    "MaintainMetabolicAdaptationStrategy.java": "analytics/projection",
    "SimulationContext.java": "analytics/projection",
    "TdeeEstimationService.java": "analytics/tdee",
    "TdeeEstimationServiceImpl.java": "analytics/tdee",
    "TdeeCalculationEngine.java": "analytics/tdee",
    "TdeeAlgorithmService.java": "analytics/tdee",
    "TdeeHistoryProvider.java": "analytics/tdee",
    "TdeeHistoryContext.java": "analytics/tdee",
    "TdeeInternalQueryPort.java": "analytics/tdee",
    
    # Analytics DTOs
    "AdherenceMetrics.java": "analytics/dto",
    "DailyCalorieAggregation.java": "analytics/dto",
    "DateValuePoint.java": "analytics/dto",
    "MetabolicForecastResponse.java": "analytics/dto",
    "MultiScenarioProjectionResponse.java": "analytics/dto",
    "ProjectionResponse.java": "analytics/dto",
    "TdeeResponse.java": "analytics/dto",
    "TrendResponse.java": "analytics/dto",
    "TrendVariations.java": "analytics/dto",
    "WeightForecastResult.java": "analytics/dto",

    # Dashboard
    "DashboardController.java": "dashboard/controller",
    "DashboardAssembler.java": "dashboard/service",
    "WeightProjectionService.java": "dashboard/service",
    "WeightProjectionServiceImpl.java": "dashboard/service",
    "MetabolicForecastService.java": "dashboard/service",
    "NutritionTargetService.java": "dashboard/service",
    "NutritionTargetResult.java": "dashboard/service",
    "DashboardResponse.java": "dashboard/dto",

    # Shared
    "ErrorResponse.java": "shared/dto",
    "PagedResponse.java": "shared/dto",
    "GlobalExceptionHandler.java": "shared/exception",
    "ResourceNotFoundException.java": "shared/exception",
    "DateRangeException.java": "shared/exception",
    "DateValidationException.java": "shared/exception",
    "MetabolicConstants.java": "shared/util",
    "NutritionalConstants.java": "shared/util",
    "NumberUtils.java": "shared/util",
    "NutritionUtils.java": "shared/util",
    "DateMapper.java": "shared/mapper",

    # Validation
    "ValidAge.java": "validation",
    "ValidAgeValidator.java": "validation",
    "ValidPassword.java": "validation",
    "PasswordConstraintValidator.java": "validation",
    "DateValidationService.java": "validation",

    # Infrastructure/Config
    "SecurityConfig.java": "infrastructure/config",
    "CorsProperties.java": "infrastructure/config",
    "ClockConfig.java": "infrastructure/config",
    "JpaAuditConfig.java": "infrastructure/config",
    "SchedulingConfig.java": "infrastructure/config",
    "SwaggerConfig.java": "infrastructure/config",
    "WebMvcConfig.java": "infrastructure/config",
    
    # Application root
    "NutritrackApplication.java": ""
}

def get_fqcn(relative_path):
    # e.g., auth/service/AuthService.java -> com.nutritrack.auth.service.AuthService
    clean_path = relative_path.replace(".java", "")
    parts = ["com", "nutritrack"] + [p for p in clean_path.split("/") if p]
    return ".".join(parts)

def main():
    fqcn_map = {}
    files_to_process = []
    
    # Pass 1: find all files and build FQCN map
    for root, dirs, files in os.walk(BASE_DIR):
        for file in files:
            if not file.endswith(".java"):
                continue
            
            old_full_path = os.path.join(root, file)
            # Remove BASE_DIR from the path to get the old relative package path
            old_rel_path = os.path.relpath(old_full_path, BASE_DIR)
            old_fqcn = get_fqcn(old_rel_path)
            
            # Determine new path
            if file in file_mapping:
                new_sub_pkg = file_mapping[file]
                new_rel_path = f"{new_sub_pkg}/{file}" if new_sub_pkg else file
                new_fqcn = get_fqcn(new_rel_path)
                new_package_decl = ".".join(["com", "nutritrack"] + [p for p in new_sub_pkg.split("/") if p])
                
                # Make sure com.nutritrack handles empty sub_pkg correctly
                if not new_sub_pkg:
                    new_package_decl = "com.nutritrack"
                    
                fqcn_map[old_fqcn] = new_fqcn
                
                files_to_process.append({
                    "old_path": old_full_path,
                    "new_path": os.path.join(BASE_DIR, new_rel_path),
                    "new_package": new_package_decl,
                    "filename": file
                })
            else:
                print(f"Warning: File {file} not found in mapping. Leaving as is.")
                
    # Sort fqcn_map by key length descending to prevent partial replacements
    sorted_fqcn_map = dict(sorted(fqcn_map.items(), key=lambda item: len(item[0]), reverse=True))
    
    # Pass 2: Read, transform, and write files
    for file_info in files_to_process:
        with open(file_info["old_path"], "r", encoding="utf-8") as f:
            content = f.read()
            
        # 1. Update package declaration
        # Find the package line
        package_pattern = re.compile(r"^package\s+([a-zA-Z0-9_.]+);", re.MULTILINE)
        content = package_pattern.sub(f"package {file_info['new_package']};", content)
        
        # 2. Update imports and any other FQCN references
        for old_fqcn, new_fqcn in sorted_fqcn_map.items():
            if old_fqcn != new_fqcn:
                # We do a text replacement for exact FQCN
                content = content.replace(old_fqcn, new_fqcn)
                
        # Handle simple import changes (e.g. wildcard imports which are rare but possible)
        # Assuming no wildcard imports for now, but FQCN string replace handles standard imports perfectly.
        
        # 3. Write to new path
        os.makedirs(os.path.dirname(file_info["new_path"]), exist_ok=True)
        with open(file_info["new_path"], "w", encoding="utf-8") as f:
            f.write(content)
            
        # 4. Remove old file
        if file_info["old_path"] != file_info["new_path"]:
            os.remove(file_info["old_path"])
            
    print("Refactoring complete. Cleaning up empty directories...")
    
    # Pass 3: Clean up empty directories
    for root, dirs, files in os.walk(BASE_DIR, topdown=False):
        for d in dirs:
            dir_path = os.path.join(root, d)
            try:
                if not os.listdir(dir_path):
                    os.rmdir(dir_path)
            except OSError:
                pass
                
    print("Cleanup finished.")

if __name__ == "__main__":
    main()
