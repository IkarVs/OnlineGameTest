package com.onlinegame.item;

import com.onlinegame.hero.Hero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HeroItemRepository extends JpaRepository<HeroItem, Long> {
    Optional<HeroItem> findByHeroAndSlot(Hero hero, ItemType slot);
}
