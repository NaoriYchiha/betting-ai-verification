package org.lytvynovych.bettingaiverification.controller;

import lombok.RequiredArgsConstructor;
import org.lytvynovych.bettingaiverification.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/database")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DatabaseManagementController {

    private final UserRepository userRepository;
    private final BetRepository betRepository;
    private final PredictionRepository predictionRepository;
    private final VerificationResultRepository verificationResultRepository;

    @DeleteMapping("/clear")
    @Transactional
    public ResponseEntity<?> clearAllData() {
        // Удаляем в порядке иерархии (сначала связанные сущности)
        verificationResultRepository.deleteAllInBatch();
        predictionRepository.deleteAllInBatch();
        betRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        return ResponseEntity.ok().build();
    }
}