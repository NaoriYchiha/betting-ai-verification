package org.lytvynovych.bettingaiverification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lytvynovych.bettingaiverification.client.ChatGptApiClient;
import org.lytvynovych.bettingaiverification.entity.Match;
import org.springframework.stereotype.Service;

@Service
public class AiPredictionService {

    private final ChatGptApiClient chatGptApiClient;
    private final ObjectMapper objectMapper;

    public AiPredictionService(ChatGptApiClient chatGptApiClient,
                               ObjectMapper objectMapper) {
        this.chatGptApiClient = chatGptApiClient;
        this.objectMapper = objectMapper;
    }

    public AiResponse generatePrediction(Match match) {

        String json = chatGptApiClient.getPrediction(buildPrompt(match));

        try {
            return objectMapper.readValue(json, AiResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

    private String buildPrompt(Match match) {
        return """
        Return ONLY JSON:

        {
          "homeWin": number,
          "draw": number,
          "awayWin": number,
          "explanation": string
        }

        Match:
        Home: %s
        Away: %s

        Sum must be 100.
        """.formatted(
                match.getHomeTeam().getName(),
                match.getAwayTeam().getName()
        );
    }
}