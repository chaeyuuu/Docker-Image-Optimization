package com.fisa.docker_optimization.controller;

import com.fisa.docker_optimization.dto.ItemDto;
import com.fisa.docker_optimization.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemApiController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemDto>> findAll() {
        return ResponseEntity.ok(itemService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String name) {
        return ResponseEntity.ok(itemService.searchByName(name));
    }

    @GetMapping("/available")
    public ResponseEntity<List<ItemDto>> findAvailable() {
        return ResponseEntity.ok(itemService.findAvailable());
    }

    @PostMapping
    public ResponseEntity<ItemDto> save(@RequestBody ItemDto dto) {
        return ResponseEntity.ok(itemService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDto> update(@PathVariable Long id,
                                          @RequestBody ItemDto dto) {
        return ResponseEntity.ok(itemService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}