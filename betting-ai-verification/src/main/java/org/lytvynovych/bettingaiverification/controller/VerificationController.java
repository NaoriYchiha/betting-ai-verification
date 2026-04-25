package org.lytvynovych.bettingaiverification.controller;

import org.lytvynovych.bettingaiverification.dto.verification.VerificationResponse;
import org.lytvynovych.bettingaiverification.entity.Bet;
import org.lytvynovych.bettingaiverification.entity.User;
import org.lytvynovych.bettingaiverification.entity.VerificationResult;
import org.lytvynovych.bettingaiverification.repository.BetRepository;
import org.lytvynovych.bettingaiverification.repository.UserRepository;
import org.lytvynovych.bettingaiverification.repository.VerificationResultRepository;
import org.lytvynovych.bettingaiverification.service.VerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/verification")
@CrossOrigin(origins = "*")
public class VerificationController {

    private final UserRepository userRepository;
    private final BetRepository betRepository;
    private final VerificationService verificationService;
    private final VerificationResultRepository verificationResultRepository;

    public VerificationController(VerificationService verificationService,
                                  VerificationResultRepository verificationResultRepository,
                                  UserRepository userRepository,
                                  BetRepository betRepository) {
        this.verificationService = verificationService;
        this.verificationResultRepository = verificationResultRepository;
        this.userRepository = userRepository;
        this.betRepository = betRepository;
    }

    @GetMapping("/user/{userId}")
    public VerificationResponse verifyUser(@PathVariable Long userId) {
        return verificationService.verifyUser(userId);
    }

    @GetMapping("/results")
    public List<VerificationResult> getResults() {
        return verificationResultRepository.findAll();
    }

    @PostMapping("/run-all")
    public ResponseEntity<Map<String, Object>> verifyAllUsers() throws InterruptedException {
        List<User> users = userRepository.findAll();

        int success = 0, skipped = 0, failed = 0;

        for (User user : users) {
            try {
                List<Bet> bets = betRepository.findByUserId(user.getId());
                if (bets.isEmpty()) { skipped++; continue; }

                verificationService.verifyUser(user.getId());
                success++;

                Thread.sleep(1000); // 1 секунда между запросами
            } catch (Exception e) {
                failed++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", users.size());
        result.put("success", success);
        result.put("skipped", skipped);
        result.put("failed", failed);

        return ResponseEntity.ok(result);
    }
}