package org.lytvynovych.bettingaiverification.controller;
import org.lytvynovych.bettingaiverification.dto.external.BetWithRiskDto;
import org.lytvynovych.bettingaiverification.entity.Bet;
import org.lytvynovych.bettingaiverification.repository.BetRepository;
import org.lytvynovych.bettingaiverification.repository.VerificationResultRepository;
import org.lytvynovych.bettingaiverification.service.CsvImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bets")
@CrossOrigin(origins = "*")
public class BetController {

    private final BetRepository betRepository;
    private final VerificationResultRepository verificationResultRepository;
    private final CsvImportService csvImportService;

    public BetController(BetRepository betRepository,
                         CsvImportService csvImportService,
                         VerificationResultRepository verificationResultRepository) {
        this.betRepository = betRepository;
        this.verificationResultRepository = verificationResultRepository;
        this.csvImportService = csvImportService;
    }

    @GetMapping
    public List<BetWithRiskDto> getBets() {
        return betRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/user/{userId}")
    public List<BetWithRiskDto> getBetsByUser(@PathVariable Long userId) {
        return betRepository.findByUserId(userId).stream().map(this::toDto).collect(Collectors.toList());
    }

    private BetWithRiskDto toDto(Bet bet) {
        BetWithRiskDto dto = new BetWithRiskDto();
        dto.setId(bet.getId());
        dto.setUsername(bet.getUser() != null ? bet.getUser().getUsername() : "—");
        dto.setHomeTeam(bet.getMatch() != null ? bet.getMatch().getHomeTeam().getName() : "?");
        dto.setAwayTeam(bet.getMatch() != null ? bet.getMatch().getAwayTeam().getName() : "?");
        dto.setAmount(bet.getAmount());
        dto.setOutcome(bet.getOutcome());

        verificationResultRepository.findLatestByBetId(bet.getId())
                .ifPresentOrElse(result -> {
                    dto.setRiskLevel(result.getRiskLevel());
                    dto.setSuspicious(result.isSuspicious());
                }, () -> {
                    dto.setRiskLevel("NOT VERIFIED");
                    dto.setSuspicious(false);
                });

        return dto;
    }

    @PostMapping("/upload-bets")
    public ResponseEntity<?> uploadBets(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "Empty file."));
        try {
            int count = csvImportService.importBetsFromCsv(file);
            return ResponseEntity.ok(Map.of("message", "Bets uploaded", "count", count));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}