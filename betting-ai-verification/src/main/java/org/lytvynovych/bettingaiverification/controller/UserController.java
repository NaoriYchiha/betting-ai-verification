package org.lytvynovych.bettingaiverification.controller;
import org.lytvynovych.bettingaiverification.dto.external.UserWithRiskDto;
import org.lytvynovych.bettingaiverification.entity.User;
import org.lytvynovych.bettingaiverification.repository.UserRepository;
import org.lytvynovych.bettingaiverification.repository.VerificationResultRepository;
import org.lytvynovych.bettingaiverification.service.CsvImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;
    private final VerificationResultRepository verificationResultRepository;
    private final CsvImportService csvImportService;

    public UserController(UserRepository userRepository,
                          VerificationResultRepository verificationResultRepository,
                            CsvImportService csvImportService) {
        this.userRepository = userRepository;
        this.verificationResultRepository = verificationResultRepository;
        this.csvImportService = csvImportService;
    }



    @PostMapping("/upload")
    public ResponseEntity<?> uploadUsers(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please upload a CSV file."));
        }

        try {
            int count = csvImportService.importUsersFromCsv(file);
            return ResponseEntity.ok(Map.of(
                    "message", "Upload successful",
                    "count", count
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public List<UserWithRiskDto> getUsers() {
        return userRepository.findAll().stream().map(user -> {
            UserWithRiskDto dto = new UserWithRiskDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setBalance(user.getBalance());
            dto.setBlocked(user.isBlocked());

            // берём последний результат верификации
            verificationResultRepository.findLatestByUserId(user.getId())
                    .ifPresentOrElse(result -> {
                        dto.setRiskLevel(result.getRiskLevel());
                        dto.setSuspicious(result.isSuspicious());
                    }, () -> {
                        dto.setRiskLevel("LOW");
                        dto.setSuspicious(false);
                    });

            return dto;
        }).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserWithRiskDto getUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserWithRiskDto dto = new UserWithRiskDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setBalance(user.getBalance());
        dto.setBlocked(user.isBlocked());

        verificationResultRepository.findLatestByUserId(user.getId())
                .ifPresentOrElse(result -> {
                    dto.setRiskLevel(result.getRiskLevel());
                    dto.setSuspicious(result.isSuspicious());
                }, () -> {
                    dto.setRiskLevel("LOW");
                    dto.setSuspicious(false);
                });

        return dto;
    }

}