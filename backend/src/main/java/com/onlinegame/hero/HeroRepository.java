package com.onlinegame.hero;

import com.onlinegame.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HeroRepository extends JpaRepository<Hero, Long> {
    List<Hero> findByPlayer(Player player);

    // Charge uniquement equippedItems + item (un seul bag JOIN FETCH)
    @Query("SELECT DISTINCT h FROM Hero h LEFT JOIN FETCH h.equippedItems ei LEFT JOIN FETCH ei.item WHERE h.id = :id")
    Optional<Hero> findByIdWithEquipment(Long id);

    // Charge uniquement unlockedTechniques (un seul bag JOIN FETCH)
    @Query("SELECT DISTINCT h FROM Hero h LEFT JOIN FETCH h.unlockedTechniques WHERE h.id = :id")
    Optional<Hero> findByIdWithTechniques(Long id);
}
