package com.onlinegame.critter;

import com.onlinegame.critter.dto.CodexEntryDTO;
import com.onlinegame.player.Player;
import com.onlinegame.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/critters")
@RequiredArgsConstructor
public class CritterController {

    private final CritterSpeciesRepository speciesRepository;
    private final CapturedCritterRepository capturedCritterRepository;
    private final PlayerRepository playerRepository;

    /** Codex complet : toutes les espèces, masquées si non capturées par le joueur. */
    @GetMapping("/codex/player/{playerId}")
    public ResponseEntity<List<CodexEntryDTO>> getCodex(@PathVariable Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Joueur introuvable"));

        List<CodexEntryDTO> codex = speciesRepository.findAllByOrderByCodexNumberAsc().stream()
                .map(species -> CodexEntryDTO.from(
                        species,
                        capturedCritterRepository.existsByPlayerAndSpecies(player, species)))
                .toList();

        return ResponseEntity.ok(codex);
    }
}
