package org.lytvynovych.bettingaiverification.dto.external;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BetWithRiskDto {
    private Long id;
    private String username;
    private String homeTeam;
    private String awayTeam;
    private double amount;
    private String outcome;
    private String riskLevel;
    private boolean suspicious;
}
