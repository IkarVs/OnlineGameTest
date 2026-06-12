package com.onlinegame.combat;

import com.onlinegame.hero.Hero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CombatRepository extends JpaRepository<Combat, Long> {
    Optional<Combat> findFirstByHeroAndStatusOrderByStartedAtDesc(Hero hero, CombatStatus status);
}
