package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsByOwner(Integer ownerId);

    ItemDto getItemById(Integer itemId, Integer userId);

    List<ItemDto> searchItems(String searchText);

    ItemDto createItem(ItemDto itemDto, Integer userId);

    ItemDto updateItem(Integer itemId, ItemDto itemDto, Integer userId);

    ItemDto deleteItem(Integer itemId, Integer userId);
}
