package org.lytvynovych.bettingaiverification.repository;

import org.lytvynovych.bettingaiverification.entity.Bet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByMatchId(Long matchId);
    List<Bet> findByUserId(Long userId);
}
