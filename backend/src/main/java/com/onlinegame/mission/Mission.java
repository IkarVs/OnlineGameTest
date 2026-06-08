package com.onlinegame.mission;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "missions")
@Getter
@Setter
@NoArgsConstructor
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;
    private int difficulty;
    private int requiredLevel = 1;
    private int xpReward;
    private int woodReward;
    private int metalReward;
    private int foodReward;

    public Mission(String name, String description, int difficulty,
                   int requiredLevel, int xpReward,
                   int woodReward, int metalReward, int foodReward) {
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.requiredLevel = requiredLevel;
        this.xpReward = xpReward;
        this.woodReward = woodReward;
        this.metalReward = metalReward;
        this.foodReward = foodReward;
    }
}
