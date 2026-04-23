package org.lytvynovych.bettingaiverification.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lytvynovych.bettingaiverification.client.GroqApiClient;
import org.lytvynovych.bettingaiverification.dto.verification.VerificationRequest;
import org.lytvynovych.bettingaiverification.dto.verification.VerificationResponse;
import org.springframework.stereotype.Service;

@Service
public class AiVerificationService {

    private final GroqApiClient client;
    private final ObjectMapper objectMapper;

    public AiVerificationService(GroqApiClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public VerificationResponse verify(VerificationRequest request) {

        try {
            String prompt = buildPrompt(request);

            // ✅ ВОТ ТВОЙ РЕАЛЬНЫЙ ВЫЗОВ
            String rawResponse = client.getPrediction(prompt).block();

            // 🔥 Groq/OpenAI ответ = JSON-обёртка
            JsonNode root = objectMapper.readTree(rawResponse);

            String content = root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

// добавь эти две строки перед readValue
            content = content.replaceAll("(?s)```json\\s*", "").replaceAll("(?s)```\\s*", "").trim();

            return objectMapper.readValue(content, VerificationResponse.class);


        } catch (Exception e) {
            throw new RuntimeException("AI verification failed", e);
        }
    }

    private String buildPrompt(VerificationRequest request) {
        return """
    You are a betting risk analysis system. Analyze the FULL betting history of a user.

    Detect patterns such as:
    - gambling addiction (frequent bets, increasing amounts, short intervals)
    - suspicious betting patterns (always same outcome, unusual amounts)
    - match fixing risk (coordinated bets on unlikely outcomes)

    User has %d bets in total. Current balance: %.2f

    Return ONLY raw JSON, no markdown, no code blocks:

    {
      "suspicious": boolean,
      "gamblingAddictionRisk": boolean,
      "matchFixingRisk": boolean,
      "riskLevel": "LOW" or "MEDIUM" or "HIGH",
      "explanation": "text"
    }

    USER BETTING HISTORY:
    """.formatted(request.getBetHistory().size(), request.getCurrentBalance())
                + toJson(request);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }
}