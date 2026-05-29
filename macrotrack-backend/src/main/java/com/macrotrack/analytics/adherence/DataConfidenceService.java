package com.macrotrack.analytics.adherence;

import com.macrotrack.user.model.User;
import com.macrotrack.weight.model.WeightEntry;
import com.macrotrack.weight.repository.WeightEntryRepository;
import com.macrotrack.nutrition.service.NutritionAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Comparator;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataConfidenceService {

    private final WeightEntryRepository weightEntryRepository;
    private final NutritionAnalyticsService nutritionAnalyticsService;

    public int computeDataConfidenceScore(User user, LocalDate targetDate) {
        LocalDate userStartDate;
        if (user.getCreatedAt() != null) {
            userStartDate = user.getCreatedAt().toLocalDate();
        } else {
            log.warn("User {} has no createdAt date, falling back to 30 days window for data confidence.", user.getId());
            userStartDate = targetDate.minusDays(30);
        }

        LocalDate from = targetDate.minusDays(30);
        
        if (userStartDate.isAfter(from)) {
            from = userStartDate;
        }

        if (from.isAfter(targetDate)) {
            log.warn("Confidence window from date {} is after target date {}, returning MINIMAL confidence.", from, targetDate);
            return ConfidenceTier.MINIMAL.getScore();
        }
        
        long validDays = weightEntryRepository.countDaysWithBothLogs(user.getId(), from, targetDate);

        long possibleDays = ChronoUnit.DAYS.between(from, targetDate) + 1;
        possibleDays = Math.min(30, Math.max(1, possibleDays));

        double fillRate = (double) validDays / possibleDays;

        return Arrays.stream(ConfidenceTier.values())
                .sorted(Comparator.comparing(ConfidenceTier::getMinFillRate).reversed())
                .filter(tier -> fillRate >= tier.getMinFillRate())
                .findFirst()
                .map(ConfidenceTier::getScore)
                .orElse(ConfidenceTier.MINIMAL.getScore());
    }
}
