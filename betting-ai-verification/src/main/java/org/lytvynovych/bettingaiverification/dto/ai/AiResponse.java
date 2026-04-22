package org.lytvynovych.bettingaiverification.dto.ai;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiResponse {

    private double homeWin;
    private double draw;
    private double awayWin;
    private String explanation;

    // getters/setters
}