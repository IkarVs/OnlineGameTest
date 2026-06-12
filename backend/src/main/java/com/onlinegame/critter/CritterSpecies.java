package com.onlinegame.critter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "critter_species")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class CritterSpecies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Numéro dans le Codex (1 à 10) */
    @Column(unique = true, nullable = false)
    private int codexNumber;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String element;

    private int baseHp;
    private int baseAttack;
    private int baseDefense;

    /** Taux de capture de base entre 0.0 (impossible) et 1.0 (toujours) */
    private double captureRate;

    /** Niveau minimum du héros pour rencontrer cette créature */
    private int minHeroLevel = 1;

    /** XP gagnée en battant cette créature */
    private int xpReward;

    /** Chemin du sprite côté frontend (ex: /critters/1.svg) */
    private String spriteUrl;

    public CritterSpecies(int codexNumber, String name, String description, String element,
                          int baseHp, int baseAttack, int baseDefense,
                          double captureRate, int minHeroLevel, int xpReward, String spriteUrl) {
        this.codexNumber = codexNumber;
        this.name = name;
        this.description = description;
        this.element = element;
        this.baseHp = baseHp;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.captureRate = captureRate;
        this.minHeroLevel = minHeroLevel;
        this.xpReward = xpReward;
        this.spriteUrl = spriteUrl;
    }
}
