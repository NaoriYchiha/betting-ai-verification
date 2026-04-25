package org.lytvynovych.bettingaiverification.service;

import org.lytvynovych.bettingaiverification.dto.verification.VerificationRequest;
import org.lytvynovych.bettingaiverification.dto.verification.VerificationResponse;
import org.lytvynovych.bettingaiverification.entity.Bet;
import org.lytvynovych.bettingaiverification.entity.Match;
import org.lytvynovych.bettingaiverification.entity.User;
import org.lytvynovych.bettingaiverification.entity.VerificationResult;
import org.lytvynovych.bettingaiverification.repository.BetRepository;
import org.lytvynovych.bettingaiverification.repository.UserRepository;
import org.lytvynovych.bettingaiverification.repository.VerificationResultRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VerificationService {

    private final AiVerificationService aiService;
    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final VerificationResultRepository verificationResultRepository;

    public VerificationService(AiVerificationService aiService,
                               BetRepository betRepository,
                               UserRepository userRepository,
                               VerificationResultRepository verificationResultRepository) {
        this.aiService = aiService;
        this.betRepository = betRepository;
        this.userRepository = userRepository;
        this.verificationResultRepository = verificationResultRepository;
    }

    public VerificationResponse verifyUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Bet> bets = betRepository.findByUserId(userId);

        if (bets.isEmpty()) {
            throw new RuntimeException("No bets found for user");
        }

        // проверяем сколько ставок уже проанализировано
        long alreadyAnalyzedCount = bets.stream()
                .filter(bet -> verificationResultRepository.findLatestByBetId(bet.getId()).isPresent())
                .count();

        // если все ставки уже проанализированы — возвращаем последний результат без вызова AI
        if (alreadyAnalyzedCount == bets.size()) {
            return verificationResultRepository
                    .findLatestByUserId(userId)
                    .map(result -> {
                        VerificationResponse cached = new VerificationResponse();
                        cached.setSuspicious(result.isSuspicious());
                        cached.setRiskLevel(result.getRiskLevel());
                        cached.setExplanation(result.getAiExplanation());
                        // парсим флаги из reason если нужно
                        cached.setGamblingAddictionRisk(
                                result.getReason() != null && result.getReason().contains("addiction")
                        );
                        cached.setMatchFixingRisk(
                                result.getReason() != null && result.getReason().contains("fixing")
                        );
                        return cached;
                    })
                    .orElseThrow(() -> new RuntimeException("No verification results found"));
        }

        // если появились новые ставки — запускаем AI только для них
        List<Bet> newBets = bets.stream()
                .filter(bet -> verificationResultRepository.findLatestByBetId(bet.getId()).isEmpty())
                .collect(Collectors.toList());

        VerificationRequest request = buildRequest(user, bets); // передаём ВСЕ ставки в промпт
        VerificationResponse response = aiService.verify(request);

        // сохраняем только для НОВЫХ ставок
        newBets.forEach(bet -> {
            VerificationResult result = new VerificationResult();
            result.setBet(bet);
            result.setSuspicious(response.isSuspicious());
            result.setRiskLevel(resolveRiskLevel(response));
            result.setReason(resolveReason(response));
            result.setAiExplanation(response.getExplanation());
            result.setCreatedAt(LocalDateTime.now());
            verificationResultRepository.save(result);
        });

        return response;
    }

    private VerificationRequest buildRequest(User user, List<Bet> bets) {
        VerificationRequest request = new VerificationRequest();
        request.setUserId(user.getId());
        request.setUsername(user.getUsername());
        request.setCurrentBalance(user.getBalance());

        List<VerificationRequest.BetDto> betDtos = bets.stream().map(bet -> {
            VerificationRequest.BetDto dto = new VerificationRequest.BetDto();
            dto.setBetId(bet.getId());
            dto.setHomeTeam(bet.getMatch().getHomeTeam().getName());
            dto.setAwayTeam(bet.getMatch().getAwayTeam().getName());
            dto.setAmount(bet.getAmount());
            dto.setOutcome(bet.getOutcome());
            dto.setCreatedAt(bet.getCreatedAt().toString());
            return dto;
        }).collect(Collectors.toList());

        request.setBetHistory(betDtos);
        return request;
    }

    private String resolveRiskLevel(VerificationResponse response) {
        String level = response.getRiskLevel();
        if (level == null) return "LOW";
        return switch (level.toUpperCase()) {
            case "HIGH" -> "HIGH";
            case "MEDIUM" -> "MEDIUM";
            default -> "LOW";
        };
    }

    private String resolveReason(VerificationResponse response) {
        if (response.isGamblingAddictionRisk()) return "Gambling addiction pattern detected";
        if (response.isMatchFixingRisk()) return "Match fixing risk detected";
        if (response.isSuspicious()) return "Suspicious betting behaviour";
        return "No significant risk";
    }
}