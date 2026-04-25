package org.lytvynovych.bettingaiverification.dto.external;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWithRiskDto {
    private Long id;
    private String username;
    private String email;
    private double balance;
    private boolean blocked;
    private String riskLevel;
    private boolean suspicious;
}