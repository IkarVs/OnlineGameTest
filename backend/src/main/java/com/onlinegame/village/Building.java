package com.onlinegame.village;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "buildings")
@Getter
@Setter
@NoArgsConstructor
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "village_id", nullable = false)
    private Village village;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BuildingType type;

    private int level = 1;

    private static final int MAX_LEVEL = 5;

    public Building(Village village, BuildingType type) {
        this.village = village;
        this.type = type;
    }

    /** Production de ressources par heure selon le niveau */
    public int getProductionPerHour() {
        return switch (type) {
            case SCIERIE -> 10 * level;
            case MINE    ->  5 * level;
            case FERME   -> 15 * level;
        };
    }

    /** Coût en bois pour passer au niveau suivant */
    public int getUpgradeCostWood() {
        return level * 100;
    }

    /** Coût en métal pour passer au niveau suivant */
    public int getUpgradeCostMetal() {
        return level * 50;
    }

    public boolean isMaxLevel() {
        return level >= MAX_LEVEL;
    }
}
