package org.lytvynovych.bettingaiverification.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Bet {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties({"bets", "password"})
    private User user;

    @ManyToOne
    @JsonIgnoreProperties({"bets"})
    private Match match;

    private double amount;

    private String outcome;

    private LocalDateTime createdAt;
}