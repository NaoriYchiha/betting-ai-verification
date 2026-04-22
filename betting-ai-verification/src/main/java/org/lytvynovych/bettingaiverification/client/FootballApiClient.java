package org.lytvynovych.bettingaiverification.client;

import org.lytvynovych.bettingaiverification.dto.external.MatchItemDto;
import org.lytvynovych.bettingaiverification.dto.external.MatchesResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class FootballApiClient {

    private final WebClient webClient;

    private final String API_KEY = "cb44fbef134d4afab75c0351cc5681b8";

    public FootballApiClient() {
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