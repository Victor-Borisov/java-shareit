package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDto> getAllByOwner(
            @RequestHeader(HEADER_USER_ID) Long ownerId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return itemService.getByOwner(ownerId, from, size);
    }

    @GetMapping("{itemId}")
    public ItemDto getById(
            @RequestHeader(HEADER_USER_ID) Long ownerId,
            @PathVariable Long itemId) {
        return itemService.getById(itemId, ownerId);
    }

    @GetMapping("search")
    public List<ItemDto> search(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return itemService.search(text, from, size);
    }

    @PostMapping
    public ItemDto create(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestBody ItemInDto itemDto) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemInDto itemDto) {
        return itemService.update(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public ItemDto delete(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable Long itemId) {
        return itemService.delete(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody CommentDto commentDto) {
        return itemService.createComment(itemId, userId, commentDto);
    }
}
