package org.lytvynovych.bettingaiverification.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class GroqApiClient {

    private final WebClient webClient;

    public GroqApiClient(@Value("${groq.api-key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<String> getPrediction(String prompt) {
        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(Map.of(
                        "model", "llama-3.3-70b-versatile",
                        "temperature", 0.2,
                        "messages", List.of(
                                Map.of("role", "system", "content", "Return ONLY JSON. No text."),
                                Map.of("role", "user", "content", prompt)
                        )
                ))
                .retrieve()
                .onStatus(status -> status.value() == 429, response ->
                        Mono.error(new RuntimeException("Groq rate limit exceeded"))
                )
                .onStatus(status -> status.value() == 401, response ->
                        Mono.error(new RuntimeException("Invalid Groq API key"))
                )
                .bodyToMono(String.class);
    }
}