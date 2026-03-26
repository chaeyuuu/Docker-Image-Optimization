package com.fisa.docker_optimization.service;

import com.fisa.docker_optimization.dto.ItemDto;
import com.fisa.docker_optimization.entity.Item;
import com.fisa.docker_optimization.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

    public List<ItemDto> findAll() {
        return itemRepository.findAll()
                .stream()
                .map(ItemDto::from)
                .collect(Collectors.toList());
    }

    public ItemDto findById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + id));
        return ItemDto.from(item);
    }

    public List<ItemDto> searchByName(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(ItemDto::from)
                .collect(Collectors.toList());
    }

    public List<ItemDto> findAvailable() {
        return itemRepository.findAvailableItems()
                .stream()
                .map(ItemDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemDto save(ItemDto dto) {
        Item item = new Item(dto.getName(), dto.getDescription(), dto.getPrice(), dto.getQuantity());
        Item saved = itemRepository.save(item);
        log.info("상품 등록: id={}, name={}", saved.getId(), saved.getName());
        return ItemDto.from(saved);
    }

    @Transactional
    public ItemDto update(Long id, ItemDto dto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + id));
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setQuantity(dto.getQuantity());
        log.info("상품 수정: id={}", id);
        return ItemDto.from(item);
    }

    @Transactional
    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다. id=" + id);
        }
        itemRepository.deleteById(id);
        log.info("상품 삭제: id={}", id);
    }

    public long countItems() {
        return itemRepository.countAllItems();
    }

    public long sumQuantity() {
        return itemRepository.sumAllQuantity();
    }
}
