package org.lytvynovych.bettingaiverification.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    private String email;

    private String password;

    private double balance;

    private LocalDateTime createdAt;

    // опционально (сильно для диплома)
    private boolean blocked;
}