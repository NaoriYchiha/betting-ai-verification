package org.lytvynovych.bettingaiverification.controller;

import org.lytvynovych.bettingaiverification.entity.Match;
import org.lytvynovych.bettingaiverification.entity.Prediction;
import org.lytvynovych.bettingaiverification.service.AiPredictionService;
import org.lytvynovych.bettingaiverification.dto.ai.AiResponse;
import org.lytvynovych.bettingaiverification.service.MatchService;
import org.lytvynovych.bettingaiverification.service.PredictionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/predictions")
@CrossOrigin(origins = "*")
public class PredictionController {

    private final AiPredictionService aiPredictionService;
    private final MatchService matchService;
    private final PredictionService predictionService;

    public PredictionController(AiPredictionService aiPredictionService,
                                MatchService matchService,
                                PredictionService predictionService) {
        this.aiPredictionService = aiPredictionService;
        this.matchService = matchService;
        this.predictionService = predictionService;
    }

    @PostMapping("/{matchId}")
    public Prediction generatePrediction(@PathVariable Long matchId) {

        Match match = matchService.getById(matchId);

        AiResponse response = aiPredictionService.generatePrediction(match);

        return predictionService.savePrediction(
                match,
                response.getHomeWin(),
                response.getDraw(),
                response.getAwayWin(),
                response.getExplanation()
        );
    }

    @GetMapping("/match/{matchId}")
    public Prediction getPrediction(@PathVariable Long matchId) {
        return predictionService.getByMatchId(matchId);
    }
}