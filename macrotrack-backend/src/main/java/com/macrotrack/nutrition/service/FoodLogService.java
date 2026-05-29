package com.macrotrack.nutrition.service;

import com.macrotrack.nutrition.dto.FoodLogRequest;
import com.macrotrack.nutrition.dto.FoodLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface FoodLogService {
    FoodLogResponse addEntry(String email, FoodLogRequest request);
    List<FoodLogResponse> getEntriesByDate(String email, LocalDate date);

    Page<FoodLogResponse> getPaginatedEntriesBetween(String email, LocalDate from, LocalDate to, Pageable pageable);
    void deleteEntry(String email, Long entryId);
    FoodLogResponse updateLoggedTime(String email, Long id, LocalTime loggedTime);
}

