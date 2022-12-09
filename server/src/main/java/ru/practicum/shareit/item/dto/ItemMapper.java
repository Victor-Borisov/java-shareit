package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;

@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto.OwnerTiny owner = ItemDto.OwnerTiny
                .builder()
                .id(item.getOwner().getId())
                .name(item.getOwner().getName())
                .build();
        Long requestId = item.getRequest() == null ? null : item.getRequest().getId();
        return ItemDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(requestId)
                .owner(owner)
                .comments(new ArrayList<>())
                .build();
    }

    public static Item fromItemInDto(ItemInDto itemDto) {
        return Item
                .builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}
