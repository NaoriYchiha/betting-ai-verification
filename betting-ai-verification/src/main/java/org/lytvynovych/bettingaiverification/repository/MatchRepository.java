package org.lytvynovych.bettingaiverification.repository;

import org.lytvynovych.bettingaiverification.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findByExternalId(Long externalId);

    List<Match> findTop20ByOrderByStartTimeAsc();
}