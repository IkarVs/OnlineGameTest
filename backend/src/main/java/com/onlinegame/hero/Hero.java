package com.onlinegame.hero;

import com.onlinegame.item.HeroItem;
import com.onlinegame.player.Player;
import com.onlinegame.technique.Technique;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "heroes")
@Getter
@Setter
@NoArgsConstructor
public class Hero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HeroClass heroClass;

    private int level = 1;
    private int experience = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @OneToMany(mappedBy = "hero", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HeroItem> equippedItems = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hero_techniques",
            joinColumns = @JoinColumn(name = "hero_id"),
            inverseJoinColumns = @JoinColumn(name = "technique_id")
    )
    private List<Technique> unlockedTechniques = new ArrayList<>();

    public Hero(String name, HeroClass heroClass, Player player) {
        this.name = name;
        this.heroClass = heroClass;
        this.player = player;
    }

    public int getXpToNextLevel() {
        return level * 100;
    }

    public void addExperience(int xp) {
        this.experience += xp;
        while (this.experience >= getXpToNextLevel()) {
            this.experience -= getXpToNextLevel();
            this.level++;
        }
    }

    public int getTotalAttack() {
        int base = 10 + (level * 2);
        return base + equippedItems.stream()
                .mapToInt(hi -> hi.getItem().getAttackBonus())
                .sum();
    }

    public int getTotalDefense() {
        int base = 5 + level;
        return base + equippedItems.stream()
                .mapToInt(hi -> hi.getItem().getDefenseBonus())
                .sum();
    }

    public int getTotalHp() {
        int base = 100 + (level * 10);
        return base + equippedItems.stream()
                .mapToInt(hi -> hi.getItem().getHpBonus())
                .sum();
    }
}
