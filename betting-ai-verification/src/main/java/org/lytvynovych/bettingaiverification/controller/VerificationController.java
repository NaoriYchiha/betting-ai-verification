package org.lytvynovych.bettingaiverification.controller;

import org.lytvynovych.bettingaiverification.dto.verification.VerificationResponse;
import org.lytvynovych.bettingaiverification.entity.VerificationResult;
import org.lytvynovych.bettingaiverification.repository.VerificationResultRepository;
import org.lytvynovych.bettingaiverification.service.VerificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/verification")
@CrossOrigin(origins = "*")
public class VerificationController {

    private final VerificationService verificationService;
    private final VerificationResultRepository verificationResultRepository;

    public VerificationController(VerificationService verificationService,
                                  VerificationResultRepository verificationResultRepository) {
        this.verificationService = verificationService;
        this.verificationResultRepository = verificationResultRepository;
    }

    @GetMapping("/user/{userId}")
    public VerificationResponse verifyUser(@PathVariable Long userId) {
        return verificationService.verifyUser(userId);
    }

    @GetMapping("/results")
    public List<VerificationResult> getResults() {
        return verificationResultRepository.findAll();
    }
}