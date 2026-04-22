package org.lytvynovych.bettingaiverification.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@Table(name = "predictions")
public class Prediction {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Match match;

    private int homeWinProb;
    private int drawProb;
    private int awayWinProb;

    @Column(length = 2000)
    private String aiExplanation;

    private LocalDateTime createdAt;
}
