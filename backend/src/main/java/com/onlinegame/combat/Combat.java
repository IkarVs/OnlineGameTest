package com.onlinegame.combat;

import com.onlinegame.critter.CritterSpecies;
import com.onlinegame.hero.Hero;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "combats")
@Getter
@Setter
@NoArgsConstructor
public class Combat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hero_id", nullable = false)
    private Hero hero;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "species_id", nullable = false)
    private CritterSpecies species;

    /** Niveau du monstre (proche du niveau du héros) */
    private int monsterLevel;

    private int monsterHp;
    private int monsterMaxHp;
    private int heroHp;
    private int heroMaxHp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CombatStatus status = CombatStatus.ACTIVE;

    private int turn = 1;

    private LocalDateTime startedAt = LocalDateTime.now();

    public Combat(Hero hero, CritterSpecies species, int monsterLevel,
                  int monsterMaxHp, int heroMaxHp) {
        this.hero = hero;
        this.species = species;
        this.monsterLevel = monsterLevel;
        this.monsterMaxHp = monsterMaxHp;
        this.monsterHp = monsterMaxHp;
        this.heroMaxHp = heroMaxHp;
        this.heroHp = heroMaxHp;
    }

    public int getMonsterAttack() {
        return species.getBaseAttack() + (monsterLevel * 2);
    }

    public int getMonsterDefense() {
        return species.getBaseDefense() + monsterLevel;
    }
}
