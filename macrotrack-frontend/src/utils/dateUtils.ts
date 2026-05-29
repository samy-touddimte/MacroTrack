import { format } from 'date-fns';

export const getLocalDateString = (): string => {
    return format(new Date(), 'yyyy-MM-dd');
};