package com.onlinegame.technique;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "techniques")
@Getter
@Setter
@NoArgsConstructor
public class Technique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;
    private int damage;
    private int manaCost;
    private int requiredLevel = 1;

    @Column(nullable = false)
    private String element;

    public Technique(String name, String description, int damage, int manaCost, int requiredLevel, String element) {
        this.name = name;
        this.description = description;
        this.damage = damage;
        this.manaCost = manaCost;
        this.requiredLevel = requiredLevel;
        this.element = element;
    }
}
