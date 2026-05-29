package com.macrotrack.auth.service;

import com.macrotrack.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupService {

    private final RefreshTokenRepository refreshTokenRepository;
    
    private final Clock clock; 

    @Scheduled(cron = "0 0 3 * * ?", zone = "UTC")
    @Transactional
    public void purgeExpiredAndRevokedTokens() {
        log.info("Token cleanup started...");
        
        LocalDateTime currentUtcTime = LocalDateTime.now(clock);
        int deletedCount = refreshTokenRepository.deleteObsoleteTokens(currentUtcTime);

        log.info("Token cleanup: {} deleted", deletedCount);
    }
}
