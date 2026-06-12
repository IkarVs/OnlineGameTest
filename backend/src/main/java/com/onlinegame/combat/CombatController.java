package com.onlinegame.combat;

import com.onlinegame.combat.dto.CombatDTO;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/combats")
@RequiredArgsConstructor
public class CombatController {

    private final CombatService combatService;

    @PostMapping("/start")
    public ResponseEntity<CombatDTO> start(@RequestBody StartCombatRequest request) {
        return ResponseEntity.ok(combatService.startCombat(request.heroId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CombatDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(combatService.getCombat(id));
    }

    @PostMapping("/{id}/attack")
    public ResponseEntity<CombatDTO> attack(@PathVariable Long id) {
        return ResponseEntity.ok(combatService.attack(id));
    }

    @PostMapping("/{id}/technique/{techniqueId}")
    public ResponseEntity<CombatDTO> useTechnique(@PathVariable Long id, @PathVariable Long techniqueId) {
        return ResponseEntity.ok(combatService.useTechnique(id, techniqueId));
    }

    @PostMapping("/{id}/capture")
    public ResponseEntity<CombatDTO> capture(@PathVariable Long id) {
        return ResponseEntity.ok(combatService.tryCapture(id));
    }

    @PostMapping("/{id}/flee")
    public ResponseEntity<CombatDTO> flee(@PathVariable Long id) {
        return ResponseEntity.ok(combatService.flee(id));
    }

    public record StartCombatRequest(@NotNull Long heroId) {}
}
