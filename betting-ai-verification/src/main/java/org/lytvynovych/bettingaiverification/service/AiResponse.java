package org.lytvynovych.bettingaiverification.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiResponse {

    private int homeWin;
    private int draw;
    private int awayWin;
    private String explanation;

    // getters/setters
}