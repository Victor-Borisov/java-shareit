package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.fromItemRequestDto;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.toItemRequestDto;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemService itemService;
    private final ItemDao itemRepository;
    private final ItemRequestDao itemRequestRepository;

    @Transactional
    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = UserMapper.fromUserDto(userService.getById(userId));
        ItemRequest itemRequest = fromItemRequestDto(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        itemRequestRepository.save(itemRequest);

        return toItemRequestDto(itemRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAllByUser(Long userId) {
        userService.getById(userId);
        List<ItemRequest> itemRequestList = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedAsc(userId);

        return getItemRequestDtoList(itemRequestList);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAll(int from, int size, Long userId) {
        userService.getById(userId);
        List<ItemRequest> itemRequestList = itemRequestRepository
                .findAllByRequestorIdNotLikeOrderByCreatedAsc(userId, PageRequest.of(from, size));

        return getItemRequestDtoList(itemRequestList);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getById(Long requestId, Long userId) {
        userService.getById(userId);
        ItemRequest itemRequest = itemRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest not found"));
        ItemRequestDto itemRequestDto = toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemService.findAllByRequestId(itemRequestDto.getId()));

        return itemRequestDto;
    }

    private List<ItemRequestDto> getItemRequestDtoList(List<ItemRequest> itemRequestList) {
        Map<ItemRequest, List<Item>> items = itemRepository
                .findAllByRequestIn(itemRequestList)
                .stream().collect(groupingBy(Item::getRequest, toList()));

        return itemRequestList.stream().map(itemRequest -> {
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
            List<Item> itemList = items.get(itemRequest);
            itemRequestDto.setItems(itemList == null ? Collections.emptyList() : itemList
                    .stream().map(ItemMapper::toItemDto).collect(toList()));

            return itemRequestDto;
        }).collect(toList());
    }
}
