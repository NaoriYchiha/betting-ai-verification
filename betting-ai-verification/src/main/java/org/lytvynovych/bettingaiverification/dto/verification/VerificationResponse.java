package org.lytvynovych.bettingaiverification.dto.verification;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationResponse {

    private boolean suspicious;
    private boolean gamblingAddictionRisk;
    private boolean matchFixingRisk;
    private String riskLevel;
    private String explanation;

    public VerificationResponse() {}
}