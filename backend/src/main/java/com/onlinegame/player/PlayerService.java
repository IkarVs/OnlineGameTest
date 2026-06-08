package com.onlinegame.player;

import com.onlinegame.village.Village;
import com.onlinegame.village.VillageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final VillageRepository villageRepository;

    @Transactional
    public Player findOrCreate(String name) {
        return playerRepository.findByName(name).orElseGet(() -> {
            Player player = playerRepository.save(new Player(name));
            Village village = new Village(player);
            villageRepository.save(village);
            return player;
        });
    }

    public Player findById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Joueur introuvable : " + id));
    }
}
