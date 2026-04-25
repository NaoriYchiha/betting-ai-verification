package org.lytvynovych.bettingaiverification.controller;

import org.lytvynovych.bettingaiverification.entity.Match;
import org.lytvynovych.bettingaiverification.service.MatchService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    // 📌 список матчей (левая панель)
    @GetMapping
    public Page<Match> getMatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return matchService.getMatches(page, size);
    }

    // 📌 один матч
    @GetMapping("/{id}")
    public Match getMatch(@PathVariable Long id) {
        return matchService.getById(id);
    }
}