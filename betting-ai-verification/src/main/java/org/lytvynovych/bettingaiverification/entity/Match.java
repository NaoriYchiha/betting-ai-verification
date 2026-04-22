package org.lytvynovych.bettingaiverification.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue
    private Long id;

    private Long externalId;

    private LocalDateTime startTime;

    private String status;

    @ManyToOne
    private Team homeTeam;

    @ManyToOne
    private Team awayTeam;
}
