package com.onlinegame.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;

    @GetMapping
    public ResponseEntity<List<Item>> getAll() {
        return ResponseEntity.ok(itemRepository.findAll());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Item>> getByType(@PathVariable ItemType type) {
        return ResponseEntity.ok(itemRepository.findByType(type));
    }
}
