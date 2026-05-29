package com.macrotrack.weight.service;

import com.macrotrack.weight.dto.WeightEntryRequest;
import com.macrotrack.weight.dto.WeightEntryResponse;
import com.macrotrack.shared.exception.ResourceNotFoundException;
import com.macrotrack.user.model.User;
import com.macrotrack.weight.model.WeightEntry;
import com.macrotrack.goal.dto.GoalResponse;
import com.macrotrack.weight.repository.WeightEntryRepository;
import com.macrotrack.user.service.UserInternalQueryPort;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.macrotrack.weight.mapper.WeightEntryMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WeightEntryServiceImpl implements WeightEntryService {

    private final UserInternalQueryPort userService;
    private final WeightEntryRepository weightEntryRepository;
    private final Clock clock;
    private final WeightEntryMapper weightEntryMapper;
    


    @Transactional
    public WeightEntryResponse addOrUpdateEntry(String email, WeightEntryRequest request) {
        User user = userService.getUserEntityByEmail(email);

        WeightEntry entry = weightEntryRepository.findByUserIdAndDate(user.getId(), request.date())
                .map(existing -> {
                    weightEntryMapper.updateEntityFromDto(request, existing);
                    return existing;
                })
                .orElseGet(() -> WeightEntry.builder()
                        .user(user)
                        .date(request.date())
                        .weightKg(request.weightKg())
                        .loggedTime(request.loggedTime() != null ? request.loggedTime() : LocalTime.now())
                        .build());

        try {
            return weightEntryMapper.toResponse(weightEntryRepository.saveAndFlush(entry));
        } catch (DataIntegrityViolationException ex) {
            log.warn("Concurrent insert detected for weight entry user {} on date {}, fetching existing", user.getId(), request.date());
            WeightEntry existing = weightEntryRepository.findByUserIdAndDate(user.getId(), request.date())
                    .orElseThrow(() -> new IllegalStateException("Failed to find weight entry after concurrent conflict"));
            weightEntryMapper.updateEntityFromDto(request, existing);
            return weightEntryMapper.toResponse(weightEntryRepository.save(existing));
        }
    }
    
    public List<WeightEntryResponse> getEntries(String email, LocalDate from, LocalDate to) {
        return getEntriesBetween(email, from, to).stream()
                .map(weightEntryMapper::toResponse)
                .toList();
    }

    @Override
    public List<WeightEntry> getEntriesBetween(String email, LocalDate from, LocalDate to) {
        User user = userService.getUserEntityByEmail(email);
        return getEntriesBetween(user.getId(), from, to);
    }

    @Override
    public List<WeightEntry> getEntriesBetween(Long userId, LocalDate from, LocalDate to) {
        return weightEntryRepository.findByUserIdAndDateBetweenOrderByDateAsc(userId, from, to);
    }

    @Override
    public List<WeightEntry> getEntriesBetweenForUser(User user, LocalDate from, LocalDate to) {
        return getEntriesBetween(user.getId(), from, to);
    }

    @Override
    public Page<WeightEntryResponse> getPaginatedEntries(String email, LocalDate from, LocalDate to, Pageable pageable) {
        User user = userService.getUserEntityByEmail(email);
        return weightEntryRepository.findAllByUserIdAndDateBetweenOrderByDateAsc(user.getId(), from, to, pageable)
                .map(weightEntryMapper::toResponse);
    }

    @Transactional
    public void deleteEntry(String email, Long entryId) {
        User user = userService.getUserEntityByEmail(email);
        int deletedCount = weightEntryRepository.deleteByIdAndUserId(entryId, user.getId());
        if (deletedCount == 0) {
            throw new ResourceNotFoundException("Weight entry not found");
        }
    }

    public Double resolveInitialWeight(User user, GoalResponse goal, Double fallbackWeight) {
        if (goal == null || goal.startDate() == null) {
            return fallbackWeight;
        }
        return weightEntryRepository.findByUserIdAndDate(user.getId(), goal.startDate())
                .map(WeightEntry::getWeightKg)
                .orElseGet(() -> {
                    List<WeightEntry> afterStart = weightEntryRepository
                            .findByUserIdAndDateBetweenOrderByDateAsc(
                                    user.getId(), goal.startDate(), LocalDate.now(clock));
                    if (!afterStart.isEmpty()) {
                        return afterStart.get(0).getWeightKg();
                    }
                    return fallbackWeight;
                });
    }

    public Double resolveCurrentWeight(User user, Map<LocalDate, Double> trend) {
        if (trend != null && !trend.isEmpty()) {
            List<Double> trendVals = new ArrayList<>(trend.values());
            return trendVals.get(trendVals.size() - 1);
        }
        return getFirstWeightKg(user);
    }

    @Override
    public Double getFirstWeightKg(User user) {
        return weightEntryRepository.findFirstByUserIdOrderByDateAsc(user.getId())
                .map(WeightEntry::getWeightKg)
                .orElse(null);
    }

    @Override
    public Double getLatestBodyFatPercentage(User user) {
        return weightEntryRepository.findTopByUserIdOrderByDateDesc(user.getId())
                .map(WeightEntry::getBodyFatPercentage)
                .orElse(null);
    }

    @Override
    public Double getLatestWeightKg(User user) {
        return weightEntryRepository.findTopByUserIdOrderByDateDesc(user.getId())
                .map(WeightEntry::getWeightKg)
                .orElse(null);
    }

    public Optional<WeightEntry> getLatestEntry(User user) {
        return weightEntryRepository.findTopByUserIdOrderByDateDesc(user.getId());
    }

    @Transactional
    public WeightEntryResponse updateLoggedTime(String email, Long id, LocalTime loggedTime) {
        User user = userService.getUserEntityByEmail(email);
        WeightEntry entry = weightEntryRepository.findById(id)
                .filter(we -> we.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Weight entry not found"));
        entry.setLoggedTime(loggedTime);
        return weightEntryMapper.toResponse(weightEntryRepository.save(entry));
    }

}
