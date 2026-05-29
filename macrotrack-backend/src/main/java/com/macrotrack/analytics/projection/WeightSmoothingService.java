package com.macrotrack.analytics.projection;

import com.macrotrack.analytics.common.DateValuePoint;
import com.macrotrack.weight.model.WeightEntry;
import com.macrotrack.shared.util.NumberUtils;
import com.macrotrack.shared.util.MetabolicConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.NavigableMap;
import java.util.TreeMap;

@Service
@Slf4j
public class WeightSmoothingService {


    public NavigableMap<LocalDate, Double> computeWeightTrend(List<WeightEntry> entries) {
        if (entries == null || entries.isEmpty()) return new TreeMap<>();

        LinkedHashMap<LocalDate, Double> dailyWeights = entries.stream()
                .sorted(Comparator.comparing(WeightEntry::getDate))
                .collect(Collectors.toMap(
                        WeightEntry::getDate,
                        WeightEntry::getWeightKg,
                        (existing, replacement) -> replacement,
                        LinkedHashMap::new
                ));

        return computeWeightTrend(dailyWeights);
    }

    public NavigableMap<LocalDate, Double> computeWeightTrend(Map<LocalDate, Double> dailyWeights) {
        if (dailyWeights == null || dailyWeights.isEmpty()) return new TreeMap<>();
        
        List<DateValuePoint<Double>> points = dailyWeights.entrySet().stream()
                .map(e -> new DateValuePoint<>(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(DateValuePoint::date))
                .toList();
                
        return computeTrendFromPoints(points);
    }

    private NavigableMap<LocalDate, Double> computeTrendFromPoints(List<DateValuePoint<Double>> points) {
        NavigableMap<LocalDate, Double> trend = new TreeMap<>();
        Double prevTrend = null;
        LocalDate prevDate = null;

        for (DateValuePoint<Double> point : points) {
            LocalDate currDate = point.date();
            Double weight = point.value();

            if (prevTrend == null || prevDate == null) {
                prevTrend = weight;
                trend.put(currDate, prevTrend);
                prevDate = currDate;
                continue;
            }

            long daysElapsed = ChronoUnit.DAYS.between(prevDate, currDate);
            if (daysElapsed > MetabolicConstants.MAX_GAP_DAYS) {
                double newTrend = weight;
                for (long day = 1; day < daysElapsed; day++) {
                    LocalDate missingDate = prevDate.plusDays(day);
                    double interpolatedTrend = prevTrend + ((newTrend - prevTrend) * day) / daysElapsed;
                    trend.put(missingDate, NumberUtils.roundToTwo(interpolatedTrend));
                }
                prevTrend = newTrend;
            } else if (daysElapsed > 0) {
                double dynamicAlpha = 1.0 - Math.pow(1.0 - MetabolicConstants.EWMA_ALPHA, daysElapsed);
                double delta = weight - prevTrend;
                double newTrend;
                
                if (Math.abs(delta) > prevTrend * 0.05) {
                    log.warn("Ignored abnormal weight outlier for smoothing: {}", weight);
                    newTrend = prevTrend;
                } else {
                    newTrend = dynamicAlpha * weight + (1.0 - dynamicAlpha) * prevTrend;
                }

                for (long day = 1; day < daysElapsed; day++) {
                    LocalDate missingDate = prevDate.plusDays(day);
                    double interpolatedTrend = prevTrend + ((newTrend - prevTrend) * day) / daysElapsed;
                    trend.put(missingDate, NumberUtils.roundToTwo(interpolatedTrend));
                }
                prevTrend = newTrend;
            }

            trend.put(currDate, NumberUtils.roundToTwo(prevTrend));
            prevDate = currDate;
        }

        return trend;
    }
}
