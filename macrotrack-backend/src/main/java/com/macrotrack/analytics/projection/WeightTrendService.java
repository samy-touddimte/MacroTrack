package com.macrotrack.analytics.projection;

import com.macrotrack.analytics.common.DateValuePoint;
import com.macrotrack.analytics.projection.TrendResponse;
import com.macrotrack.user.model.User;
import com.macrotrack.weight.model.WeightEntry;
import com.macrotrack.weight.repository.WeightEntryRepository;
import com.macrotrack.user.service.UserInternalQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.macrotrack.shared.util.NumberUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeightTrendService {

    private final UserInternalQueryPort userService;
    private final WeightEntryRepository weightEntryRepository;
    private final WeightSmoothingService weightSmoothingService;

    public TrendResponse getWeightTrend(String email, LocalDate from, LocalDate to) {
        User user = userService.getUserEntityByEmail(email);

        List<WeightEntry> entries = weightEntryRepository.findByUserIdAndDateBetweenOrderByDateAsc(
                user.getId(), from, to);

        NavigableMap<LocalDate, Double> trendMap = weightSmoothingService.computeWeightTrend(entries);
        
        List<DateValuePoint<Double>> dynamicTrend = trendMap.entrySet().stream()
                .map(entry -> new DateValuePoint<>(entry.getKey(), entry.getValue()))
                .toList();

        Double variation7d = null;
        Double variation30d = null;

        if (!trendMap.isEmpty()) {
            LocalDate latestDate = trendMap.lastKey();
            double latestTrend = trendMap.get(latestDate);
            
            Map.Entry<LocalDate, Double> entry7d = trendMap.floorEntry(latestDate.minusDays(7));
            if (entry7d != null) {
                variation7d = NumberUtils.roundToTwo(latestTrend - entry7d.getValue());
            }
            
            Map.Entry<LocalDate, Double> entry30d = trendMap.floorEntry(latestDate.minusDays(30));
            if (entry30d != null) {
                variation30d = NumberUtils.roundToTwo(latestTrend - entry30d.getValue());
            }
        }

        return new TrendResponse(dynamicTrend, new TrendVariations(variation7d, variation30d));
    }


}
