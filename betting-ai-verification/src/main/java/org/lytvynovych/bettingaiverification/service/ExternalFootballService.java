package org.lytvynovych.bettingaiverification.service;


import org.lytvynovych.bettingaiverification.client.FootballApiClient;
import org.lytvynovych.bettingaiverification.dto.external.MatchItemDto;
import org.lytvynovych.bettingaiverification.dto.external.TeamDto;
import org.lytvynovych.bettingaiverification.entity.Match;
import org.lytvynovych.bettingaiverification.entity.Team;
import org.lytvynovych.bettingaiverification.repository.MatchRepository;
import org.lytvynovych.bettingaiverification.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ExternalFootballService {

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final FootballApiClient footballApiClient;

    public ExternalFootballService(TeamRepository teamRepository,
                                   MatchRepository matchRepository,
                                   FootballApiClient footballApiClient) {
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
        this.footballApiClient = footballApiClient;
    }

    @Transactional
    public void syncMatches() {

        List<MatchItemDto> apiMatches = footballApiClient.getTodayMatches();
        System.out.println("API RESPONSE: " + apiMatches);

        System.out.println("API SIZE: " + apiMatches.size());
        for (MatchItemDto dto : apiMatches) {

            // 🔹 1. Команды из DTO
            TeamDto homeDto = dto.getHomeTeam();
            TeamDto awayDto = dto.getAwayTeam();

            if (homeDto == null || awayDto == null) {
                continue; // защита от кривых данных
            }

            // 🔹 2. Найти или создать HOME
            Team home = teamRepository.findByExternalId(homeDto.getId())
                    .orElseGet(() -> {
                        Team t = new Team();
                        t.setName(homeDto.getName());
                        t.setExternalId(homeDto.getId());
                        return teamRepository.save(t);
                    });

            // 🔹 3. Найти или создать AWAY
            Team away = teamRepository.findByExternalId(awayDto.getId())
                    .orElseGet(() -> {
                        Team t = new Team();
                        t.setName(awayDto.getName());
                        t.setExternalId(awayDto.getId());
                        return teamRepository.save(t);
                    });

            // 🔹 4. Проверка на дубликат матча
            if (matchRepository.findByExternalId(dto.getId()).isPresent()) {
                continue;
            }

            // 🔹 5. Парсинг даты (ВАЖНО!)
            LocalDateTime startTime = parseDate(dto.getUtcDate());

            // 🔹 6. Создание матча
            Match match = new Match();
            match.setExternalId(dto.getId());
            match.setHomeTeam(home);
            match.setAwayTeam(away);
            match.setStartTime(startTime);
            match.setStatus("UPCOMING");
            System.out.println("API MATCHES: " + apiMatches.size());
            matchRepository.save(match);
        }
    }

    // 🔥 Парсинг даты из API
    private LocalDateTime parseDate(String utcDate) {
        try {
            return OffsetDateTime.parse(utcDate).toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }
}