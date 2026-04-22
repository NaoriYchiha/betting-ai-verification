package org.lytvynovych.bettingaiverification.controller;

import org.lytvynovych.bettingaiverification.service.ExternalFootballService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final ExternalFootballService externalFootballService;

    public AdminController(ExternalFootballService externalFootballService) {
        this.externalFootballService = externalFootballService;
    }

    // 🚀 запуск синхронизации матчей
    @PostMapping("/sync-matches")
    public String syncMatches() {

        externalFootballService.syncMatches();

        return "MATCHES SYNCED SUCCESSFULLY";
    }
}