package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getByOwner(Long ownerId, int from, int size);

    ItemDto getById(Long itemId, Long ownerId);

    List<ItemDto> search(String searchText, int from, int size);

    ItemDto create(ItemInDto itemDto, Long userId);

    ItemDto update(ItemInDto itemDto, Long itemId, Long userId);

    ItemDto delete(Long itemId, Long userId);

    CommentDto createComment(Long itemId, Long userId, CommentDto commentDto);

    List<ItemDto> findAllByRequestId(Long requestId);
}
