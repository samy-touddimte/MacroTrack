package com.macrotrack.weight.service;

import com.macrotrack.goal.dto.GoalResponse;
import com.macrotrack.shared.exception.ResourceNotFoundException;
import com.macrotrack.user.model.User;
import com.macrotrack.user.service.UserInternalQueryPort;
import com.macrotrack.weight.dto.WeightEntryRequest;
import com.macrotrack.weight.dto.WeightEntryResponse;
import com.macrotrack.weight.mapper.WeightEntryMapper;
import com.macrotrack.weight.model.WeightEntry;
import com.macrotrack.weight.repository.WeightEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeightEntryServiceImplTest {

    @Mock private UserInternalQueryPort userService;
    @Mock private WeightEntryRepository weightEntryRepository;
    @Mock private Clock clock;
    @Mock private WeightEntryMapper weightEntryMapper;

    @InjectMocks
    private WeightEntryServiceImpl weightEntryService;

    private User testUser;
    private final String testEmail = "test@test.com";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail(testEmail);
    }

    @Test
    void addOrUpdateEntry_newEntry_createsEntry() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        LocalTime time = LocalTime.of(8, 0);
        WeightEntryRequest request = new WeightEntryRequest(date, 80.0, 20.0, time);
        Instant now = Instant.now();
        
        when(userService.getUserEntityByEmail(testEmail)).thenReturn(testUser);
        when(weightEntryRepository.findByUserIdAndDate(1L, date)).thenReturn(Optional.empty());
        
        WeightEntry savedEntry = new WeightEntry();
        when(weightEntryRepository.saveAndFlush(any(WeightEntry.class))).thenReturn(savedEntry);
        
        WeightEntryResponse response = new WeightEntryResponse(1L, date, 80.0, 20.0, now, time);
        when(weightEntryMapper.toResponse(savedEntry)).thenReturn(response);

        WeightEntryResponse result = weightEntryService.addOrUpdateEntry(testEmail, request);
        
        assertEquals(response, result);
        verify(weightEntryMapper, never()).updateEntityFromDto(any(), any());
    }

    @Test
    void addOrUpdateEntry_existingEntry_updatesEntry() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        WeightEntryRequest request = new WeightEntryRequest(date, 80.0, 20.0, null);
        Instant now = Instant.now();
        
        when(userService.getUserEntityByEmail(testEmail)).thenReturn(testUser);
        WeightEntry existingEntry = new WeightEntry();
        when(weightEntryRepository.findByUserIdAndDate(1L, date)).thenReturn(Optional.of(existingEntry));
        
        WeightEntry savedEntry = new WeightEntry();
        when(weightEntryRepository.saveAndFlush(existingEntry)).thenReturn(savedEntry);
        
        WeightEntryResponse response = new WeightEntryResponse(1L, date, 80.0, 20.0, now, null);
        when(weightEntryMapper.toResponse(savedEntry)).thenReturn(response);

        WeightEntryResponse result = weightEntryService.addOrUpdateEntry(testEmail, request);
        
        assertEquals(response, result);
        verify(weightEntryMapper).updateEntityFromDto(request, existingEntry);
    }

    @Test
    void addOrUpdateEntry_concurrentInsert_recoversAndUpdates() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        WeightEntryRequest request = new WeightEntryRequest(date, 80.0, 20.0, null);
        Instant now = Instant.now();
        
        when(userService.getUserEntityByEmail(testEmail)).thenReturn(testUser);
        when(weightEntryRepository.findByUserIdAndDate(1L, date)).thenReturn(Optional.empty());
        when(weightEntryRepository.saveAndFlush(any(WeightEntry.class))).thenThrow(new DataIntegrityViolationException("duplicate"));
        
        WeightEntry concurrentExisting = new WeightEntry();
        when(weightEntryRepository.findByUserIdAndDate(1L, date)).thenReturn(Optional.empty()).thenReturn(Optional.of(concurrentExisting));
        
        WeightEntry savedEntry = new WeightEntry();
        when(weightEntryRepository.save(concurrentExisting)).thenReturn(savedEntry);
        
        WeightEntryResponse response = new WeightEntryResponse(1L, date, 80.0, 20.0, now, null);
        when(weightEntryMapper.toResponse(savedEntry)).thenReturn(response);

        WeightEntryResponse result = weightEntryService.addOrUpdateEntry(testEmail, request);
        
        assertEquals(response, result);
        verify(weightEntryMapper).updateEntityFromDto(request, concurrentExisting);
    }

    @Test
    void getEntries_returnsMappedList() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);
        when(userService.getUserEntityByEmail(testEmail)).thenReturn(testUser);
        
        WeightEntry entry = new WeightEntry();
        when(weightEntryRepository.findByUserIdAndDateBetweenOrderByDateAsc(1L, from, to)).thenReturn(List.of(entry));
        
        WeightEntryResponse response = new WeightEntryResponse(1L, from, 80.0, 20.0, null, null);
        when(weightEntryMapper.toResponse(entry)).thenReturn(response);
        
        List<WeightEntryResponse> results = weightEntryService.getEntries(testEmail, from, to);
        assertEquals(1, results.size());
        assertEquals(response, results.get(0));
    }

    @Test
    void getEntriesBetween_byEmail_returnsList() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);
        when(userService.getUserEntityByEmail(testEmail)).thenReturn(testUser);
        when(weightEntryRepository.findByUserIdAndDateBetweenOrderByDateAsc(1L, from, to)).thenReturn(List.of());
        
        List<WeightEntry> results = weightEntryService.getEntriesBetween(testEmail, from, to);
        assertTrue(results.isEmpty());
    }

    @Test
    void getEntriesBetweenForUser_returnsList() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);
        when(weightEntryRepository.findByUserIdAndDateBetweenOrderByDateAsc(1L, from, to)).thenReturn(List.of());
        
        List<WeightEntry> results = weightEntryService.getEntriesBetweenForUser(testUser, from, to);
        assertTrue(results.isEmpty());
    }

    @Test
    void getPaginatedEntries_returnsMappedPage() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getUserEntityByEmail(testEmail)).thenReturn(testUser);
        
        WeightEntry entry = new WeightEntry();
        Page<WeightEntry> page = new PageImpl<>(List.of(entry));
        when(weightEntryRepository.findAllByUserIdAndDateBetweenOrderByDateAsc(1L, from, to, pageable)).thenReturn(page);
        
        WeightEntryResponse response = new WeightEntryResponse(1L, from, 80.0, 20.0, null, null);
        when(weightEntryMapper.toResponse(entry)).thenReturn(response);
        
        Page<WeightEntryResponse> results = weightEntryService.getPaginatedEntries(testEmail, from, to, pageable);
        assertEquals(1, results.getTotalElements());
        assertEquals(response, results.getContent().get(0));
    }

    @Test
    void deleteEntry_existing_deletes() {
        when(userService.getUserEntityByEmail(testEmail)).thenReturn(testUser);
        when(weightEntryRepository.deleteByIdAndUserId(10L, 1L)).thenReturn(1);
        
        assertDoesNotThrow(() -> weightEntryService.deleteEntry(testEmail, 10L));
    }

    @Test
    void deleteEntry_notExisting_throws() {
        when(userService.getUserEntityByEmail(testEmail)).thenReturn(testUser);
        when(weightEntryRepository.deleteByIdAndUserId(10L, 1L)).thenReturn(0);
        
        assertThrows(ResourceNotFoundException.class, () -> weightEntryService.deleteEntry(testEmail, 10L));
    }

    @Test
    void resolveInitialWeight_noGoal_returnsFallback() {
        assertEquals(80.0, weightEntryService.resolveInitialWeight(testUser, null, 80.0));
    }

    @Test
    void resolveInitialWeight_withGoalEntryFound_returnsEntryWeight() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        GoalResponse goal = new GoalResponse(1L, 75.0, -0.5, startDate, true);
        
        WeightEntry entry = new WeightEntry();
        entry.setWeightKg(82.0);
        when(weightEntryRepository.findByUserIdAndDate(1L, startDate)).thenReturn(Optional.of(entry));
        
        assertEquals(82.0, weightEntryService.resolveInitialWeight(testUser, goal, 80.0));
    }

    @Test
    void resolveInitialWeight_withGoalNoEntry_findsNextEntry() {
        when(clock.instant()).thenReturn(Instant.parse("2026-01-10T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
        
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        GoalResponse goal = new GoalResponse(1L, 75.0, -0.5, startDate, true);
        
        when(weightEntryRepository.findByUserIdAndDate(1L, startDate)).thenReturn(Optional.empty());
        
        WeightEntry entry = new WeightEntry();
        entry.setWeightKg(83.0);
        when(weightEntryRepository.findByUserIdAndDateBetweenOrderByDateAsc(1L, startDate, LocalDate.of(2026, 1, 10)))
                .thenReturn(List.of(entry));
        
        assertEquals(83.0, weightEntryService.resolveInitialWeight(testUser, goal, 80.0));
    }

    @Test
    void resolveCurrentWeight_hasTrend_returnsLast() {
        Map<LocalDate, Double> trend = new TreeMap<>();
        trend.put(LocalDate.of(2026, 1, 1), 81.0);
        trend.put(LocalDate.of(2026, 1, 2), 80.5);
        
        assertEquals(80.5, weightEntryService.resolveCurrentWeight(testUser, trend));
    }

    @Test
    void resolveCurrentWeight_emptyTrend_returnsFirst() {
        WeightEntry entry = new WeightEntry();
        entry.setWeightKg(80.0);
        when(weightEntryRepository.findFirstByUserIdOrderByDateAsc(1L)).thenReturn(Optional.of(entry));
        
        assertEquals(80.0, weightEntryService.resolveCurrentWeight(testUser, Map.of()));
    }

    @Test
    void getLatestBodyFatPercentage_returnsValue() {
        WeightEntry entry = new WeightEntry();
        entry.setBodyFatPercentage(15.0);
        when(weightEntryRepository.findTopByUserIdOrderByDateDesc(1L)).thenReturn(Optional.of(entry));
        
        assertEquals(15.0, weightEntryService.getLatestBodyFatPercentage(testUser));
    }

    @Test
    void getLatestWeightKg_returnsValue() {
        WeightEntry entry = new WeightEntry();
        entry.setWeightKg(80.0);
        when(weightEntryRepository.findTopByUserIdOrderByDateDesc(1L)).thenReturn(Optional.of(entry));
        
        assertEquals(80.0, weightEntryService.getLatestWeightKg(testUser));
    }

    @Test
    void getLatestEntry_returnsValue() {
        WeightEntry entry = new WeightEntry();
        when(weightEntryRepository.findTopByUserIdOrderByDateDesc(1L)).thenReturn(Optional.of(entry));
        
        assertTrue(weightEntryService.getLatestEntry(testUser).isPresent());
    }

    @Test
    void updateLoggedTime_valid_updatesAndReturns() {
        when(userService.getUserEntityByEmail(testEmail)).thenReturn(testUser);
        
        WeightEntry entry = new WeightEntry();
        entry.setUser(testUser);
        when(weightEntryRepository.findById(10L)).thenReturn(Optional.of(entry));
        
        WeightEntry saved = new WeightEntry();
        when(weightEntryRepository.save(entry)).thenReturn(saved);
        
        WeightEntryResponse response = new WeightEntryResponse(10L, null, 80.0, null, null, LocalTime.of(9, 0));
        when(weightEntryMapper.toResponse(saved)).thenReturn(response);
        
        WeightEntryResponse result = weightEntryService.updateLoggedTime(testEmail, 10L, LocalTime.of(9, 0));
        assertEquals(response, result);
    }

    @Test
    void updateLoggedTime_invalidUser_throws() {
        when(userService.getUserEntityByEmail(testEmail)).thenReturn(testUser);
        
        User otherUser = new User();
        otherUser.setId(2L);
        WeightEntry entry = new WeightEntry();
        entry.setUser(otherUser);
        
        when(weightEntryRepository.findById(10L)).thenReturn(Optional.of(entry));
        
        assertThrows(ResourceNotFoundException.class, () -> weightEntryService.updateLoggedTime(testEmail, 10L, LocalTime.now()));
    }
}
