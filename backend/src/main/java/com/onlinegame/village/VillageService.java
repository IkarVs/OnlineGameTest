package com.onlinegame.village;

import com.onlinegame.player.Player;
import com.onlinegame.player.PlayerRepository;
import com.onlinegame.village.dto.VillageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VillageService {

    private final VillageRepository villageRepository;
    private final BuildingRepository buildingRepository;
    private final PlayerRepository playerRepository;

    public VillageDTO getVillage(Long playerId) {
        Village village = getVillageByPlayerId(playerId);
        return VillageDTO.from(village);
    }

    @Transactional
    public VillageDTO collectResources(Long playerId) {
        Village village = getVillageByPlayerId(playerId);

        long hoursElapsed = Duration.between(village.getLastCollected(), LocalDateTime.now()).toMinutes();
        // Calcul en minutes pour que la collecte soit visible rapidement (1/60 d'heure)
        double hoursDecimal = hoursElapsed / 60.0;

        for (Building building : village.getBuildings()) {
            int produced = (int) (building.getProductionPerHour() * hoursDecimal);
            switch (building.getType()) {
                case SCIERIE -> village.setWood(village.getWood() + produced);
                case MINE    -> village.setMetal(village.getMetal() + produced);
                case FERME   -> village.setFood(village.getFood() + produced);
            }
        }

        village.setLastCollected(LocalDateTime.now());
        villageRepository.save(village);
        return VillageDTO.from(village);
    }

    @Transactional
    public VillageDTO upgradeBuilding(Long playerId, BuildingType type) {
        Village village = getVillageByPlayerId(playerId);
        Building building = village.getBuildings().stream()
                .filter(b -> b.getType() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Bâtiment introuvable : " + type));

        if (building.isMaxLevel()) {
            throw new IllegalStateException("Ce bâtiment est déjà au niveau maximum");
        }

        int costWood = building.getUpgradeCostWood();
        int costMetal = building.getUpgradeCostMetal();

        if (village.getWood() < costWood) {
            throw new IllegalStateException("Pas assez de bois (requis : " + costWood + ")");
        }
        if (village.getMetal() < costMetal) {
            throw new IllegalStateException("Pas assez de métal (requis : " + costMetal + ")");
        }

        village.setWood(village.getWood() - costWood);
        village.setMetal(village.getMetal() - costMetal);
        building.setLevel(building.getLevel() + 1);

        villageRepository.save(village);
        return VillageDTO.from(village);
    }

    private Village getVillageByPlayerId(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Joueur introuvable"));
        return villageRepository.findByPlayer(player)
                .orElseThrow(() -> new IllegalArgumentException("Village introuvable pour ce joueur"));
    }
}
