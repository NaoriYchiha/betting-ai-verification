package org.lytvynovych.bettingaiverification.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Team {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Long externalId;

    public Team() {
    }

    public Team(String name, Long externalId) {
        this.name = name;
        this.externalId = externalId;
    }
}
