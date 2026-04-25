package org.lytvynovych.bettingaiverification.repository;

import org.lytvynovych.bettingaiverification.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    List<Prediction> findByMatchId(Long matchId);
    Optional<Prediction> findFirstByMatchIdOrderByCreatedAtDesc(Long matchId);
    String deletePredictionByMatchId(Long matchId);
}