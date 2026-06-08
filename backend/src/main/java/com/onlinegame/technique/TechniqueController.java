package com.onlinegame.technique;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/techniques")
@RequiredArgsConstructor
public class TechniqueController {

    private final TechniqueRepository techniqueRepository;

    @GetMapping
    public ResponseEntity<List<Technique>> getAll() {
        return ResponseEntity.ok(techniqueRepository.findAll());
    }
}
