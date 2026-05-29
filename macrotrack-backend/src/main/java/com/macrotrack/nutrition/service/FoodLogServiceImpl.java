package com.macrotrack.nutrition.service;

import com.macrotrack.user.service.UserInternalQueryPort;

import com.macrotrack.nutrition.dto.FoodLogRequest;
import com.macrotrack.nutrition.dto.FoodLogResponse;
import com.macrotrack.shared.exception.ResourceNotFoundException;
import com.macrotrack.nutrition.model.FoodLog;
import com.macrotrack.user.model.User;
import com.macrotrack.nutrition.repository.FoodLogRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import com.macrotrack.nutrition.mapper.FoodLogMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FoodLogServiceImpl implements FoodLogService {

    private final UserInternalQueryPort userService;
    private final FoodLogRepository foodLogRepository;
    private final FoodLogMapper foodLogMapper;

    @Transactional
    public FoodLogResponse addEntry(String email, FoodLogRequest request) {
        User user = userService.getUserEntityByEmail(email);
        FoodLog foodLog = foodLogMapper.toEntity(request);
        foodLog.setUser(user);

        if (foodLog.getLoggedTime() == null) {
            foodLog.setLoggedTime(LocalTime.now());
        }

        FoodLog savedFoodLog = foodLogRepository.save(foodLog);
        return foodLogMapper.toResponse(savedFoodLog);
    }

    public List<FoodLogResponse> getEntriesByDate(String email, LocalDate date) {
        User user = userService.getUserEntityByEmail(email);
        List<FoodLog> logs = foodLogRepository.findByUserIdAndDateOrderByCreatedAtAsc(user.getId(), date);
        return logs.stream()
                .map(foodLogMapper::toResponse)
                .toList();
    }


    public Page<FoodLogResponse> getPaginatedEntriesBetween(String email, LocalDate from, LocalDate to, Pageable pageable) {
        User user = userService.getUserEntityByEmail(email);
        return foodLogRepository.findAllByUserIdAndDateBetweenOrderByDateAsc(user.getId(), from, to, pageable)
                .map(foodLogMapper::toResponse);
    }

    @Transactional
    public void deleteEntry(String email, Long entryId) {
        User user = userService.getUserEntityByEmail(email);
        int deletedCount = foodLogRepository.deleteByIdAndUserId(entryId, user.getId());
        if (deletedCount == 0) {
            throw new ResourceNotFoundException("Food log not found");
        }
    }

    @Transactional
    public FoodLogResponse updateLoggedTime(String email, Long id, LocalTime loggedTime) {
        User user = userService.getUserEntityByEmail(email);
        FoodLog foodLog = foodLogRepository.findById(id)
                .filter(fl -> fl.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Food log not found"));
        foodLog.setLoggedTime(loggedTime);
        return foodLogMapper.toResponse(foodLogRepository.save(foodLog));
    }


}

