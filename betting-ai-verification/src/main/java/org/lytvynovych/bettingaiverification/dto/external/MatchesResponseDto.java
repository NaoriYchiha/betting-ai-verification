package org.lytvynovych.bettingaiverification.dto.external;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MatchesResponseDto {
    private List<MatchItemDto> matches;
}