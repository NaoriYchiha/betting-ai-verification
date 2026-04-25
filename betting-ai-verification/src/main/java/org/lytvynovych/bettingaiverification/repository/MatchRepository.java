package org.lytvynovych.bettingaiverification.repository;

import org.lytvynovych.bettingaiverification.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findByExternalId(Long externalId);

    List<Match> findTop20ByOrderByStartTimeDesc();

    @Modifying
    @Query("UPDATE Match m SET m.status = 'FINISHED' WHERE m.status <> 'FINISHED' AND m.startTime <= :threshold")
    int finishStaleMatches(@Param("threshold") LocalDateTime threshold);
}