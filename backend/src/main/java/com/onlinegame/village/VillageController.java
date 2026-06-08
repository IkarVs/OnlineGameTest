package com.onlinegame.village;

import com.onlinegame.village.dto.VillageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/villages")
@RequiredArgsConstructor
public class VillageController {

    private final VillageService villageService;

    @GetMapping("/player/{playerId}")
    public ResponseEntity<VillageDTO> getVillage(@PathVariable Long playerId) {
        return ResponseEntity.ok(villageService.getVillage(playerId));
    }

    @PostMapping("/player/{playerId}/collect")
    public ResponseEntity<VillageDTO> collect(@PathVariable Long playerId) {
        return ResponseEntity.ok(villageService.collectResources(playerId));
    }

    @PostMapping("/player/{playerId}/buildings/{buildingType}/upgrade")
    public ResponseEntity<VillageDTO> upgrade(
            @PathVariable Long playerId,
            @PathVariable BuildingType buildingType) {
        return ResponseEntity.ok(villageService.upgradeBuilding(playerId, buildingType));
    }
}
