package com.macrotrack.weight.controller;

import com.macrotrack.weight.dto.WeightEntryRequest;
import com.macrotrack.weight.dto.WeightEntryResponse;
import com.macrotrack.weight.dto.WeightEntryUpdateRequest;
import com.macrotrack.shared.dto.PagedResponse;
import com.macrotrack.weight.service.WeightEntryService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.macrotrack.validation.DateValidationService;

@RestController
@RequestMapping("/api/v1/weight-entries")
@Tag(name = "Weight Entries", description = "Weight tracking management")
@RequiredArgsConstructor
@Validated
public class WeightEntryController {

    private final WeightEntryService weightEntryService;
    private final DateValidationService dateValidationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add or update a weight entry")
    public WeightEntryResponse addOrUpdateEntry(@CurrentUserEmail String email, @Valid @RequestBody WeightEntryRequest request) {
        return weightEntryService.addOrUpdateEntry(email, request);
    }

    @GetMapping
    @Operation(summary = "Get weight history for a date range")
    public PagedResponse<WeightEntryResponse> getEntries(@CurrentUserEmail String email,
                                                @PastOrPresent @RequestParam("from") LocalDate from,
                                                @RequestParam("to") LocalDate to,
                                                @PageableDefault(size = 20) Pageable pageable) {
        dateValidationService.validateDateRange(from, to);

        dateValidationService.validatePastDateLimit(from);
        dateValidationService.validatePastDateLimit(to);
        return PagedResponse.of(weightEntryService.getPaginatedEntries(email, from, to, pageable));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a weight entry's logged time")
    public WeightEntryResponse updateEntry(@CurrentUserEmail String email, @PathVariable("id") Long id, @Valid @RequestBody WeightEntryUpdateRequest request) {
        return weightEntryService.updateLoggedTime(email, id, request.loggedTime());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a weight entry")
    public void deleteEntry(@CurrentUserEmail String email, @PathVariable("id") Long id) {
        weightEntryService.deleteEntry(email, id);
    }
}

