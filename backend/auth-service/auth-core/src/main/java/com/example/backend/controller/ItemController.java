package com.example.backend.controller;


import com.example.auth.api.ItemApi;
import com.example.auth.dto.ItemDTO;
import com.example.backend.entity.Item;
import com.example.backend.mapper.ItemMapper;
import com.example.backend.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemController implements ItemApi {

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Override
    public ResponseEntity<List<ItemDTO>> getAllItems() {
        List<Item> items = itemService.getAllItems();

        return ResponseEntity.ok(itemMapper.toDto(items));
    }

    @Override
    public ResponseEntity<ItemDTO> getItemById(@PathVariable Long id) {
        Item item = itemService.getItemById(id);
        return ResponseEntity.ok(itemMapper.toDto(item));
    }

    @Override
    public ResponseEntity<ItemDTO> createItem(@Valid @RequestBody ItemDTO itemDto) {
        Item item = itemMapper.toEntity(itemDto);
        itemService.createItem(itemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemMapper.toDto(item));
    }

    @Override
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @Valid @RequestBody ItemDTO itemDto) {
        Item item= itemService.updateItem(id, itemDto);
        return ResponseEntity.ok(itemMapper.toDto(item));
    }

    @Override
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
