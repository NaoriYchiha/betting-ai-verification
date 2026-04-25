package org.lytvynovych.bettingaiverification.service;

import org.lytvynovych.bettingaiverification.entity.Match;
import org.lytvynovych.bettingaiverification.entity.Prediction;
import org.lytvynovych.bettingaiverification.repository.PredictionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PredictionService {

    private final PredictionRepository predictionRepository;

    public PredictionService(PredictionRepository predictionRepository) {
        this.predictionRepository = predictionRepository;
    }

    public Prediction savePrediction(Match match,
                                     double homeWin,
                                     double draw,
                                     double awayWin,
                                     String explanation) {

        Prediction prediction = new Prediction();
        prediction.setMatch(match);
        prediction.setHomeWinProb(homeWin);
        prediction.setDrawProb(draw);
        prediction.setAwayWinProb(awayWin);
        prediction.setAiExplanation(explanation);
        prediction.setCreatedAt(LocalDateTime.now());

        return predictionRepository.save(prediction);
    }

    public Prediction getByMatchId(Long matchId) {
        return predictionRepository.findFirstByMatchIdOrderByCreatedAtDesc(matchId)
                .orElse(null);
    }

    public String deletePredictionByMatchId(Long matchId) {
        Prediction prediction = getByMatchId(matchId);
        predictionRepository.delete(prediction);
        return "Successfully deleted prediction";
    }
}