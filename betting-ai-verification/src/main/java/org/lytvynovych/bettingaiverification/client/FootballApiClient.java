package org.lytvynovych.bettingaiverification.client;

import org.lytvynovych.bettingaiverification.dto.external.MatchItemDto;
import org.lytvynovych.bettingaiverification.dto.external.MatchesResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class FootballApiClient {

    private final WebClient webClient;

    public FootballApiClient(@Value("${football.api-key}") String API_KEY) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.football-data.org/v4")
                .defaultHeader("X-Auth-Token", API_KEY) // ✅ ВОТ ТУТ ИСПРАВЛЕНИЕ
                .build();
    }

    public List<MatchItemDto> getTodayMatches() {

        MatchesResponseDto response = webClient.get()
                .uri("/matches")
                .retrieve()
                .bodyToMono(MatchesResponseDto.class)
                .block();

        return response != null ? response.getMatches() : List.of();
    }
}