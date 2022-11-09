package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getByOwner(Long ownerId);

    ItemDto getById(Long itemId, Long ownerId);

    List<ItemDto> search(String searchText);

    ItemDto create(ItemRequestDto itemDto, Long userId);

    ItemDto update(ItemRequestDto itemDto, Long itemId, Long userId);

    ItemDto delete(Long itemId, Long userId);

    CommentDto createComment(Long itemId, Long userId, CommentDto commentDto);
}
