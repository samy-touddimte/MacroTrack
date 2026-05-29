package com.macrotrack.nutrition.controller;

import com.macrotrack.nutrition.dto.FoodLogRequest;
import com.macrotrack.nutrition.dto.FoodLogResponse;
import com.macrotrack.nutrition.dto.FoodLogUpdateRequest;
import com.macrotrack.shared.dto.PagedResponse;
import com.macrotrack.nutrition.service.FoodLogService;
import com.macrotrack.auth.security.CurrentUserEmail;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.PastOrPresent;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.macrotrack.validation.DateValidationService;

@RestController
@RequestMapping("/api/v1/food-logs")
@Tag(name = "Food Logs", description = "Nutrition tracking management")
@RequiredArgsConstructor
@Validated
public class FoodLogController {

    private final FoodLogService foodLogService;
    private final DateValidationService dateValidationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a food log entry")
    public FoodLogResponse addEntry(@CurrentUserEmail String email, @Valid @RequestBody FoodLogRequest request) {
        return foodLogService.addEntry(email, request);
    }

    @GetMapping(params = "date")
    @Operation(summary = "Get food log entries for a given date")
    public List<FoodLogResponse> getEntriesByDate(@CurrentUserEmail String email, @PastOrPresent @RequestParam("date") LocalDate date) {
        validateDate(date);
        return foodLogService.getEntriesByDate(email, date);
    }

    @GetMapping(params = {"from", "to"})
    @Operation(summary = "Get food log entries for a date range")
    public PagedResponse<FoodLogResponse> getEntriesBetween(@CurrentUserEmail String email,
                                                   @PastOrPresent @RequestParam("from") LocalDate from,
                                                   @RequestParam("to") LocalDate to,
                                                   @PageableDefault(size = 20) Pageable pageable) {
        validateDateRangeWithConstraints(from, to);

        return PagedResponse.of(foodLogService.getPaginatedEntriesBetween(email, from, to, pageable));
    }

    private void validateDateRangeWithConstraints(LocalDate from, LocalDate to) {
        dateValidationService.validateDateRange(from, to);
        validateDate(from);
        validateDate(to);
    }

    private void validateDate(LocalDate date) {
        dateValidationService.validatePastDateLimit(date);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a food log entry's logged time")
    public FoodLogResponse updateEntry(@CurrentUserEmail String email, @PathVariable("id") Long id, @Valid @RequestBody FoodLogUpdateRequest request) {
        return foodLogService.updateLoggedTime(email, id, request.loggedTime());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a food log entry")
    public void deleteEntry(@CurrentUserEmail String email, @PathVariable("id") Long id) {
        foodLogService.deleteEntry(email, id);
    }
}

