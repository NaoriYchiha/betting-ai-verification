package org.lytvynovych.bettingaiverification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lytvynovych.bettingaiverification.client.FootballApiClient;
import org.lytvynovych.bettingaiverification.dto.external.MatchItemDto;
import org.lytvynovych.bettingaiverification.repository.MatchRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchScheduler {

    private final MatchRepository matchRepository;
    private final ExternalFootballService externalFootballService;

    // Inject your service that handles match and team persistence logic
    // private final MatchService matchService;

    /**
     * Task 1: Automatically close stale matches (every 10 minutes).
     * Uses a 120-minute heuristic from the start time.
     */
    @Scheduled(fixedRate = 600000)
    @Transactional
    public void closeFinishedMatches() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(120);
        int updatedCount = matchRepository.finishStaleMatches(threshold);

        if (updatedCount > 0) {
            log.info("Automatically finished {} matches based on 120min heuristic.", updatedCount);
        }
    }

    /**
     * Task 2: Synchronize new matches from the external API (every 10 minutes).
     * initialDelay = 10000 ensures the app is fully started before the first sync.
     */
    @Scheduled(initialDelay = 10000, fixedRate = 600000)
    @Transactional
    public void syncNewMatches() {
        log.info("Starting synchronization of new matches from external API...");

        try {
            externalFootballService.syncMatches();

        } catch (Exception e) {
            log.error("Error during match synchronization: {}", e.getMessage(), e);
        }
    }
}