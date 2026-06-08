package com.onlinegame.item;

import com.onlinegame.hero.Hero;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hero_items")
@Getter
@Setter
@NoArgsConstructor
public class HeroItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hero_id", nullable = false)
    private Hero hero;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType slot;

    public HeroItem(Hero hero, Item item) {
        this.hero = hero;
        this.item = item;
        this.slot = item.getType();
    }
}
