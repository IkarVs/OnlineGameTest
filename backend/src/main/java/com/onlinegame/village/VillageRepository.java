package com.onlinegame.village;

import com.onlinegame.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VillageRepository extends JpaRepository<Village, Long> {
    Optional<Village> findByPlayer(Player player);
}
