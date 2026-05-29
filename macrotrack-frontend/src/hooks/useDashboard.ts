import { useQuery } from '@tanstack/react-query';
import { fetchDashboard, fetchProjection } from '../services/analyticsApi';
import type { DashboardData, ProjectionData } from '../types';
import { useCallback } from 'react';

export const useDashboard = () => {
  const { data: dashboardData, isLoading: isDashboardLoading, error: dashboardError, refetch: refetchDashboard } = useQuery<DashboardData>({
    queryKey: ['dashboard'],
    queryFn: fetchDashboard,
  });

  const { data: projectionData, isLoading: isProjectionLoading, error: projectionError, refetch: refetchProjection } = useQuery<ProjectionData>({
    queryKey: ['projection'],
    queryFn: fetchProjection,
  });

  const isLoading = isDashboardLoading || isProjectionLoading;
  const errorObj = dashboardError || projectionError;
  const error = errorObj instanceof Error ? errorObj.message : (errorObj ? String(errorObj) : null);

  const refetchAll = useCallback(() => {
    refetchDashboard();
    refetchProjection();
  }, [refetchDashboard, refetchProjection]);

  return {
    data: dashboardData || null,
    projection: projectionData || null,
    isLoading,
    error,
    refetchAll,
  };
};