package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDto> getAllByOwner(@RequestHeader(HEADER_USER_ID) Long ownerId,
                                       @RequestParam(defaultValue = "0")
                                       @Min(value = 0, message = "Parameter from must not be negative") int from,
                                       @RequestParam(defaultValue = "10")
                                       @Min(value = 1, message = "Parameter size must be positive") int size) {
        return itemService.getByOwner(ownerId, from, size);
    }

    @GetMapping("{itemId}")
    public ItemDto getById(@RequestHeader(HEADER_USER_ID) Long ownerId,
                           @PathVariable Long itemId) {
        return itemService.getById(itemId, ownerId);
    }

    @GetMapping("search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam(defaultValue = "0")
                                @Min(value = 0, message = "Parameter from must not be negative") int from,
                                @RequestParam(defaultValue = "10")
                                @Min(value = 1, message = "Parameter size must be positive") int size) {
        return itemService.search(text, from, size);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(HEADER_USER_ID) Long userId,
                          @Validated({Marker.OnCreate.class}) @RequestBody ItemInDto itemDto) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(HEADER_USER_ID) Long userId,
                          @PathVariable Long itemId,
                          @Validated(Marker.OnUpdate.class) @RequestBody ItemInDto itemDto) {
        return itemService.update(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public ItemDto delete(@RequestHeader(HEADER_USER_ID) Long userId,
                          @PathVariable Long itemId) {
        return itemService.delete(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return itemService.createComment(itemId, userId, commentDto);
    }
}
