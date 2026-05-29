package com.macrotrack.goal.service;

import com.macrotrack.goal.dto.GoalRequest;
import com.macrotrack.goal.dto.GoalResponse;
import com.macrotrack.shared.exception.ResourceNotFoundException;
import com.macrotrack.goal.model.Goal;
import com.macrotrack.user.model.User;
import com.macrotrack.goal.repository.GoalRepository;
import com.macrotrack.weight.service.WeightEntryService;
import com.macrotrack.user.service.UserInternalQueryPort;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.macrotrack.goal.mapper.GoalMapper;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GoalServiceImpl implements GoalService, GoalInternalQueryPort {

    private final UserInternalQueryPort userService;
    private final GoalRepository goalRepository;
    private final WeightEntryService weightEntryService;
    private final Clock clock;
    private final GoalMapper goalMapper;



    @Transactional
    public GoalResponse createGoal(String email, GoalRequest request) {
        User user = userService.getUserEntityByEmail(email);

        Double currentWeight = weightEntryService.getLatestWeightKg(user);
        log.info("GoalServiceImpl: currentWeight resolved as {} for user {}", currentWeight, user.getEmail());

        if (currentWeight == null && (request.targetWeightKg() != null || request.weeklyRateKg() != null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A current weight must be recorded before setting a goal");
        }

        if (request.targetWeightKg() != null && request.weeklyRateKg() != null) {
            GoalValidator.validateGoal(currentWeight, request.targetWeightKg(), request.weeklyRateKg());
        }

        goalRepository.deactivateActiveGoals(user.getId());

        Goal newGoal = Goal.builder()
                .user(user)
                .targetWeightKg(request.targetWeightKg())
                .weeklyRateKg(request.weeklyRateKg())
                .startDate(request.startDate() != null ? request.startDate() : LocalDate.now(clock))
                .active(true)
                .build();

        try {
            newGoal = goalRepository.saveAndFlush(newGoal);
            
        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violation when saving goal for user {}: {}", email, e.getMessage());
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, 
                "Cannot save goal due to database constraint violation"
            );
        }

        return goalMapper.toResponse(newGoal);
    }

    public GoalResponse getActiveGoal(String email) {
        User user = userService.getUserEntityByEmail(email);
        Goal goal = goalRepository.findByUserIdAndActiveTrue(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Active goal not found for current user"));
        return goalMapper.toResponse(goal);
    }

    public Optional<GoalResponse> findActiveGoalByEmail(String email) {
        User user = userService.getUserEntityByEmail(email);
        return findActiveGoal(user);
    }

    @Transactional
    public GoalResponse updateGoal(String email, Long goalId, GoalRequest request) {
        User user = userService.getUserEntityByEmail(email);
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }

        goalMapper.updateEntityFromDto(request, goal);
        
        if (goal.getTargetWeightKg() != null && goal.getWeeklyRateKg() != null) {
            Double currentWeight = weightEntryService.getLatestWeightKg(user);
            if (currentWeight == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A current weight must be recorded to validate goal update");
            }
            GoalValidator.validateGoal(currentWeight, goal.getTargetWeightKg(), goal.getWeeklyRateKg());
        }

        return goalMapper.toResponse(goalRepository.save(goal));
    }

    public Optional<GoalResponse> findActiveGoal(User user) {
        return goalRepository.findByUserIdAndActiveTrue(user.getId())
                .map(goalMapper::toResponse);
    }

    public List<GoalResponse> getGoalHistory(String email) {
        User user = userService.getUserEntityByEmail(email);
        return goalRepository.findByUserIdOrderByStartDateDesc(user.getId())
                .stream()
                .map(goalMapper::toResponse)
                .toList();
    }

    @Transactional
    public void deleteGoal(String email, Long goalId) {
        User user = userService.getUserEntityByEmail(email);
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }
        
        goalRepository.delete(goal);
    }
}
