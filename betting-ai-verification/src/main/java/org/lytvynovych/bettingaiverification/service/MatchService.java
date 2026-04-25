package org.lytvynovych.bettingaiverification.service;

import org.lytvynovych.bettingaiverification.entity.Match;
import org.lytvynovych.bettingaiverification.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public List<Match> getTopMatches() {
        return matchRepository.findTop20ByOrderByStartTimeDesc();
    }

    public Match getById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));
    }

    public Match save(Match match) {
        return matchRepository.save(match);
    }
}