package org.lytvynovych.bettingaiverification.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.lytvynovych.bettingaiverification.client.GroqApiClient;
import org.lytvynovych.bettingaiverification.entity.Match;
import org.lytvynovych.bettingaiverification.dto.ai.AiResponse;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AiPredictionService {

    private final GroqApiClient geminiApiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<Long, Long> lastRequestTime = new ConcurrentHashMap<>();

    public AiResponse generatePrediction(Match match) {
        Long matchId = match.getId();

        if (lastRequestTime.containsKey(matchId)) {
            long last = lastRequestTime.get(matchId);
            if (System.currentTimeMillis() - last < 15000) {
                throw new RuntimeException("Please wait before generating again (rate limit)");
            }
        }

        lastRequestTime.put(matchId, System.currentTimeMillis());

        String prompt = buildPrompt(match);

        String rawResponse = geminiApiClient
                .getPrediction(prompt)
                .block();

        return parseResponse(rawResponse);
    }

    private String buildPrompt(Match match) {
        return """
                Match: %s vs %s
                Return ONLY JSON, no extra text:
                {
                  "homeWin": number between 0 and 100,
                  "draw": number between 0 and 100,
                  "awayWin": number between 0 and 100,
                  "explanation": string
                }
                """.formatted(match.getHomeTeam().getName(), match.getAwayTeam().getName());
    }

    private AiResponse parseResponse(String rawResponse) {
        try {
            // Groq возвращает обёртку OpenAI-формата, достаём content
            JsonNode root = objectMapper.readTree(rawResponse);
            String content = root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            // убираем возможные ```json``` обёртки
            content = content.replaceAll("```json", "").replaceAll("```", "").trim();

            JsonNode ai = objectMapper.readTree(content);

            AiResponse response = new AiResponse();
            response.setHomeWin(ai.path("homeWin").asDouble());
            response.setDraw(ai.path("draw").asDouble());
            response.setAwayWin(ai.path("awayWin").asDouble());
            response.setExplanation(ai.path("explanation").asText());
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Groq response: " + e.getMessage() + "\nRaw: " + rawResponse);
        }
    }
}