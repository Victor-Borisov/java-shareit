package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsByOwner(Long ownerId);

    ItemDto getItemById(Long itemId, Long ownerId);

    List<ItemDto> searchItems(String searchText);

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    ItemDto deleteItem(Long itemId, Long userId);

    CommentDto createComment(Long itemId, Long userId, CommentDto commentDto);
}
