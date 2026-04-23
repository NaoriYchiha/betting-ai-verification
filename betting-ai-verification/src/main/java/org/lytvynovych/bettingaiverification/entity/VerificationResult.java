package org.lytvynovych.bettingaiverification.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
public class VerificationResult {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties({"verificationResults"})
    private Bet bet;

    private boolean suspicious;

    private String riskLevel; // LOW / MEDIUM / HIGH

    private String reason; // короткая причина

    @Column(length = 5000)
    private String aiExplanation;

    private LocalDateTime createdAt;
}