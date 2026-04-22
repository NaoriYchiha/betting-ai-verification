package org.lytvynovych.bettingaiverification.dto.external;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExternalMatchDto {

    private Long matchId;

    private Long homeTeamId;
    private String homeTeamName;

    private Long awayTeamId;
    private String awayTeamName;

    private LocalDateTime startTime;

    // getters/setters
}