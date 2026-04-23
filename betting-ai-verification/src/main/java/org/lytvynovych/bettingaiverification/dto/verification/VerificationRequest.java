package org.lytvynovych.bettingaiverification.dto.verification;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VerificationRequest {

    private Long userId;
    private String username;
    private double currentBalance;
    private List<BetDto> betHistory;

    @Getter
    @Setter
    public static class BetDto {
        private Long betId;
        private String homeTeam;
        private String awayTeam;
        private double amount;
        private String outcome;
        private String createdAt;
    }
}