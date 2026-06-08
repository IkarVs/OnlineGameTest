package com.onlinegame.hero;

import com.onlinegame.hero.dto.HeroDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/heroes")
@RequiredArgsConstructor
public class HeroController {

    private final HeroService heroService;

    @PostMapping
    public ResponseEntity<HeroDTO> create(@Valid @RequestBody CreateHeroRequest request) {
        return ResponseEntity.ok(heroService.create(request.name(), request.heroClass(), request.playerId()));
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<HeroDTO>> getByPlayer(@PathVariable Long playerId) {
        return ResponseEntity.ok(heroService.findByPlayer(playerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HeroDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(heroService.findById(id));
    }

    @PostMapping("/{id}/equip")
    public ResponseEntity<HeroDTO> equip(@PathVariable Long id, @RequestBody EquipRequest request) {
        return ResponseEntity.ok(heroService.equipItem(id, request.itemId()));
    }

    @DeleteMapping("/{id}/equip/{slot}")
    public ResponseEntity<HeroDTO> unequip(@PathVariable Long id, @PathVariable String slot) {
        return ResponseEntity.ok(heroService.unequipSlot(id, slot));
    }

    @PostMapping("/{id}/techniques/{techniqueId}")
    public ResponseEntity<HeroDTO> unlockTechnique(@PathVariable Long id, @PathVariable Long techniqueId) {
        return ResponseEntity.ok(heroService.unlockTechnique(id, techniqueId));
    }

    public record CreateHeroRequest(
            @NotBlank String name,
            @NotNull HeroClass heroClass,
            @NotNull Long playerId
    ) {}

    public record EquipRequest(@NotNull Long itemId) {}
}
