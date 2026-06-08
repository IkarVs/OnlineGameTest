package com.onlinegame.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType type;

    private int attackBonus = 0;
    private int defenseBonus = 0;
    private int hpBonus = 0;
    private int requiredLevel = 1;

    public Item(String name, String description, ItemType type,
                int attackBonus, int defenseBonus, int hpBonus, int requiredLevel) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus;
        this.hpBonus = hpBonus;
        this.requiredLevel = requiredLevel;
    }
}
