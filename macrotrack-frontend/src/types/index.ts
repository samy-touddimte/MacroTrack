// ── Common ────────────────────────────────────────

export interface PaginatedResponse<T> {
  content: T[];
}

// ── Auth & Users ──────────────────────────────────
export interface ProfilFormData {
  email: string;
  username: string;
  heightCm: number;
  birthDate: string;
  sex: string;
  activityLevel: string;
}

export interface UpdateUserRequest {
  username?: string;
  heightCm: number;
  birthDate: string;
  sex: string;
  activityLevel: string;
  password?: string;
}

export interface ObjectifForm {
  currentWeightKg: number;
  targetWeightKg: number;
  startDate: string;
  weeklyRateKg: number;
}

export interface User {
  id: number;
  email: string;
  username: string;
  heightCm?: number;
  birthDate?: string;
  sex?: string;
  activityLevel?: string;
  createdAt: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  username: string;
  email: string;
}

export interface RegisterRequest {
  email: string;
  username: string;
  password?: string;
  heightCm: number;
  birthDate: string; // ISO format
  sex: 'MALE' | 'FEMALE';
  activityLevel: 'SEDENTARY' | 'MODERATELY_ACTIVE' | 'VERY_ACTIVE';
  currentWeightKg: number;
  targetWeightKg: number;
  weeklyRateKg: number;
  bodyFatPercentage?: number;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface ChangePasswordRequest {
  currentPassword?: string;
  newPassword?: string;
}

// ── Weight ────────────────────────────────────────

export interface WeightEntry {
  id: number;
  date: string; // ISO format YYYY-MM-DD
  weightKg: number;
  bodyFatPercentage?: number;
  createdAt: string;
  loggedTime?: string; // format HH:mm
}

// ── Nutrition ─────────────────────────────────────

export interface FoodLog {
  id: number;
  date: string;
  foodName?: string;
  caloriesKcal: number;
  proteinG?: number;
  carbsG?: number;
  fatG?: number;
  loggedTime?: string; // format HH:mm
}

// ── Goal ──────────────────────────────────────────

export interface Goal {
  id: number;
  targetWeightKg: number;
  weeklyRateKg: number;
  startDate: string;
  active: boolean;
}

export interface WeightTrendData {
  dynamicTrend: Array<{ date: string; value: number }>;
  variations?: {
    variation7d: number;
    variation30d: number;
  };
}

// ── Analytics ────────────────────────────────────

export interface TdeeHistoryItem {
  tdeeEstimated: Array<{ date: string; value: number }>;
  caloriesConsumedKcal: Array<{ date: string; value: number }>;
  currentTdee?: number;
  isEmpirical?: boolean;
}

export interface TdeeData {
  tdeeEstimated: Array<{ date: string; value: number }>;
  caloriesConsumedKcal: Array<{ date: string; value: number }>;
  currentTdee?: number;
  isEmpirical?: boolean;
}

export interface MacroTargets {
  proteinG: number;
  fatG: number;
  carbsG: number;
}

export interface DashboardData {
  dailyCalorieTarget?: number;
  currentTdee?: number;
  todayCaloriesKcal: number;
  latestWeight?: number;
  activeGoal?: Goal;
  macroTargets?: MacroTargets;
  todayMacros?: MacroTargets;
  confidence?: number;
  adherence?: AdherenceMetrics;
}

export interface AdherenceMetrics {
  adherence7d: number;
  adherence14d: number;
  adherence30d: number;

  insufficientData?: boolean;
  loggedAdherence?: number;
}

export interface ProjectionData {
  points: Array<{ date: string; value: number }>;
  targetReachedDate?: string;
  extremeDeficit?: boolean;
}

export interface MultiScenarioProjectionResponse {
  idealPoints: Array<{ date: string; value: number }>;
  empiricalPoints: Array<{ date: string; value: number }>;
  idealDate?: string;
  empiricalDate?: string;
  hasEnoughDataForEmpirical?: boolean;
  extremeDeficit?: boolean;
}

export interface MetabolicForecastResponse {
  forecastPoints: Array<{ date: string; value: number }>;
  estimatedReachDate?: string;
  extremeDeficit?: boolean;
}
