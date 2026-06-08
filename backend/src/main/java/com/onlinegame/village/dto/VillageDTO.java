package com.onlinegame.village.dto;

import com.onlinegame.village.Building;
import com.onlinegame.village.BuildingType;
import com.onlinegame.village.Village;

import java.util.List;

public record VillageDTO(
        Long id,
        Long playerId,
        int wood,
        int metal,
        int food,
        List<BuildingDTO> buildings
) {
    public record BuildingDTO(
            Long id,
            BuildingType type,
            int level,
            int productionPerHour,
            int upgradeCostWood,
            int upgradeCostMetal,
            boolean maxLevel
    ) {
        public static BuildingDTO from(Building b) {
            return new BuildingDTO(
                    b.getId(), b.getType(), b.getLevel(),
                    b.getProductionPerHour(),
                    b.getUpgradeCostWood(), b.getUpgradeCostMetal(),
                    b.isMaxLevel()
            );
        }
    }

    public static VillageDTO from(Village v) {
        return new VillageDTO(
                v.getId(),
                v.getPlayer().getId(),
                v.getWood(),
                v.getMetal(),
                v.getFood(),
                v.getBuildings().stream().map(BuildingDTO::from).toList()
        );
    }
}
