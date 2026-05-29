import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getFoodLogsByDate, deleteFoodLog } from '../services/foodLogApi';

export function useDailyLogs(dateStr: string) {
  const queryClient = useQueryClient();

  const { data: foodLogs = [], isLoading: loadingFood } = useQuery({
    queryKey: ['editDay-food', dateStr],
    queryFn: () => getFoodLogsByDate(dateStr),
  });

  const invalidateAll = () => {
    queryClient.invalidateQueries({ queryKey: ['editDay-food', dateStr] });
    queryClient.invalidateQueries({ queryKey: ['dashboard'] });
    queryClient.invalidateQueries({ queryKey: ['calendar'] });
  };

  const deleteFoodMut = useMutation({
    mutationFn: (id: number) => deleteFoodLog(id),
    onSuccess: invalidateAll,
  });

  return {
    foodLogs,
    isLoading: loadingFood,
    deleteFoodLog: deleteFoodMut.mutate,
    isDeleting: deleteFoodMut.isPending,
  };
}
