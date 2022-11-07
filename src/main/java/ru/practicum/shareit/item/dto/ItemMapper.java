package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                      .id(item.getId())
                      .name(item.getName())
                      .description(item.getDescription())
                      .available(item.getAvailable())
                      .request(item.getRequest() != null ? item.getRequest() : null)
                      .owner(item.getOwner())
                      .comments(new ArrayList<>())
                      .build();
    }

    public static Item fromItemDto(ItemDto itemDto, User owner) {
        return Item.builder()
                   .id(itemDto.getId())
                   .name(itemDto.getName())
                   .description(itemDto.getDescription())
                   .available(itemDto.getAvailable())
                   .owner(owner)
                   .request(itemDto.getRequest())
                   .build();
    }
}
