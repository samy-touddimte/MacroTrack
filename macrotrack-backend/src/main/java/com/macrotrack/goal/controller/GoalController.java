package com.macrotrack.goal.controller;

import com.macrotrack.goal.model.Goal;

import com.macrotrack.goal.dto.GoalRequest;
import com.macrotrack.goal.dto.GoalResponse;
import com.macrotrack.goal.service.GoalService;
import com.macrotrack.auth.security.CurrentUserEmail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.macrotrack.validation.DateValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/goals")
@Tag(name = "Goals", description = "Goal management")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final DateValidationService dateValidationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new goal")
    public GoalResponse createGoal(@CurrentUserEmail String email, @Valid @RequestBody GoalRequest request) {
        if (request.startDate() != null) {
            dateValidationService.validatePastDateLimit(request.startDate());
        }
        return goalService.createGoal(email, request);
    }

    @GetMapping("/active")
    @Operation(summary = "Get the active goal")
    public org.springframework.http.ResponseEntity<GoalResponse> getActiveGoal(@CurrentUserEmail String email) {
        return goalService.findActiveGoalByEmail(email)
            .map(org.springframework.http.ResponseEntity::ok)
            .orElseGet(() -> org.springframework.http.ResponseEntity.noContent().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing goal")
    public GoalResponse updateGoal(@CurrentUserEmail String email, @PathVariable("id") Long id, @Valid @RequestBody GoalRequest request) {
        if (request.startDate() != null) {
            dateValidationService.validatePastDateLimit(request.startDate());
        }
        return goalService.updateGoal(email, id, request);
    }

    @GetMapping("/history")
    @Operation(summary = "Get goal history")
    public List<GoalResponse> getGoalHistory(@CurrentUserEmail String email) {
        return goalService.getGoalHistory(email);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an existing goal")
    public void deleteGoal(@CurrentUserEmail String email, @PathVariable("id") Long id) {
        goalService.deleteGoal(email, id);
    }
}
