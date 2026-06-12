package com.onlinegame.critter;

import com.onlinegame.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CapturedCritterRepository extends JpaRepository<CapturedCritter, Long> {
    List<CapturedCritter> findByPlayerOrderByCapturedAtDesc(Player player);
    boolean existsByPlayerAndSpecies(Player player, CritterSpecies species);
}
