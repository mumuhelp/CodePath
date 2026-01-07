package com.example.backend.mapper;

import com.example.auth.dto.ItemDTO;
import com.example.backend.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemDTO toDto(Item item);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "createdAt",ignore = true)
    Item toEntity(ItemDTO itemDTO);


    List<ItemDTO> toDto(List<Item> items);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(ItemDTO dto, @MappingTarget Item entity);
}
