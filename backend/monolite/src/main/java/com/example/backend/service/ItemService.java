package com.example.backend.service;

import com.example.backend.dto.ItemDto;
import com.example.backend.entity.Item;
import com.example.backend.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
    }

    public Item createItem(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        return itemRepository.save(item);
    }

    public Item updateItem(Long id, ItemDto itemDto) {
        Item item = getItemById(id);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}
