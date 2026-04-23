package org.lytvynovych.bettingaiverification.dto.verification;

import lombok.Getter;
import lombok.Setter;
import org.lytvynovych.bettingaiverification.entity.Bet;

import java.util.List;

@Getter
@Setter
public class BetAnalysisContext {

    private Bet bet;
    private List<Bet> userRecentBets;
    private List<Bet> matchBets;

    private double avgUserBet;
    private double totalOnOutcome;

}