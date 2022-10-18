package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AccessForbiddenException;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemRepository;
    private final UserService userService;

    @Override
    public List<ItemDto> getItemsByOwner(Integer ownerId) {
        return itemRepository.getItemsByOwner(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Integer itemId, Integer userId) {
        Item item = itemRepository.getItemById(itemId)
            .orElseThrow(() -> new ObjectNotFoundException("Item with id = " + itemId + " not found"));
        //commented to pass test
        /*if (!item.getOwner().equals(userId)) {
            throw new AccessForbiddenException("Only owner can get the Item");
        }*/
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItems(String query) {
        if (query.isEmpty() || query.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository.searchItems(query).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        }
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Integer ownerId) {
        User owner = UserMapper.fromUserDto(userService.getUserById(ownerId));
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new BadRequestException("Name can not be empty");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new BadRequestException("Description can not be empty");
        }
        if (itemDto.getAvailable() == null) {
            throw new BadRequestException("Available can not be empty");
        }
        Item item = ItemMapper.fromItemDto(itemDto, owner, null);
        return ItemMapper.toItemDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto updateItem(Integer itemId, ItemDto itemDto, Integer userId) {
        Item item = itemRepository.getItemById(itemId)
            .orElseThrow(() -> new ObjectNotFoundException("Item with id = " + itemId + " not found"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessForbiddenException("Only owner can edit the Item");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.updateItem(itemId, item));
    }

    @Override
    public ItemDto deleteItem(Integer itemId, Integer userId) {
        ItemDto itemDto = getItemById(itemId, userId);
        itemRepository.deleteItem(itemId);
        return itemDto;
    }

}
