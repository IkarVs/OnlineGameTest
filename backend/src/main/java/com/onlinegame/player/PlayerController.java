package com.onlinegame.player;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping("/login")
    public ResponseEntity<Player> login(@Valid @RequestBody LoginRequest request) {
        Player player = playerService.findOrCreate(request.name());
        return ResponseEntity.ok(player);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getById(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.findById(id));
    }

    public record LoginRequest(@NotBlank String name) {}
}
