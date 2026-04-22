package org.lytvynovych.bettingaiverification.dto.external;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchItemDto {

    private Long id;
    private String utcDate;

    private TeamDto homeTeam;
    private TeamDto awayTeam;
}