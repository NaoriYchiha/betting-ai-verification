package org.lytvynovych.bettingaiverification.controller;
import org.lytvynovych.bettingaiverification.entity.Bet;
import org.lytvynovych.bettingaiverification.repository.BetRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bets")
@CrossOrigin(origins = "*")
public class BetController {

    private final BetRepository betRepository;

    public BetController(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    @GetMapping
    public List<Bet> getBets() {
        return betRepository.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<Bet> getBetsByUser(@PathVariable Long userId) {
        return betRepository.findByUserId(userId);
    }
}