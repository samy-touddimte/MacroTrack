package com.macrotrack.weight.service;

import com.macrotrack.weight.dto.WeightEntryRequest;
import com.macrotrack.weight.dto.WeightEntryResponse;
import com.macrotrack.user.model.User;
import com.macrotrack.weight.model.WeightEntry;
import com.macrotrack.goal.dto.GoalResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface WeightEntryService {
    WeightEntryResponse addOrUpdateEntry(String email, WeightEntryRequest request);
    List<WeightEntryResponse> getEntries(String email, LocalDate from, LocalDate to);
    Page<WeightEntryResponse> getPaginatedEntries(String email, LocalDate from, LocalDate to, Pageable pageable);
    void deleteEntry(String email, Long entryId);
    Double resolveInitialWeight(User user, GoalResponse goal, Double fallbackWeight);
    Double resolveCurrentWeight(User user, Map<LocalDate, Double> trend);
    Double getFirstWeightKg(User user);
    Double getLatestBodyFatPercentage(User user);
    Double getLatestWeightKg(User user);
    List<WeightEntry> getEntriesBetween(String email, LocalDate from, LocalDate to);
    List<WeightEntry> getEntriesBetween(Long userId, LocalDate from, LocalDate to);
    java.util.Optional<WeightEntry> getLatestEntry(User user);
    List<WeightEntry> getEntriesBetweenForUser(User user, LocalDate from, LocalDate to);
    WeightEntryResponse updateLoggedTime(String email, Long id, LocalTime loggedTime);
}
