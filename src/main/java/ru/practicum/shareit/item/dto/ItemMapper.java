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
        ItemDto.ItemRequestTiny itemRequestShort = item.getRequest() == null ? null : ItemDto.ItemRequestTiny
                .builder()
                .id(item.getRequest().getId())
                .description(item.getRequest().getDescription())
                .build();
        return ItemDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(itemRequestShort)
                .owner(owner)
                .comments(new ArrayList<>())
                .build();
    }

    public static Item fromItemRequestDto(ItemRequestDto itemDto) {
        return Item
                .builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}
