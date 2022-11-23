package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.dto.ItemRequestMapper.fromItemRequestDto;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.toItemRequestDto;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;

    private final ItemService itemService;
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
        List<ItemRequestDto> itemRequestDtoList = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedAsc(userId)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        itemRequestDtoList.forEach(this::setItemsToItemRequestDto);

        return itemRequestDtoList;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAll(int from, int size, Long userId) {
        userService.getById(userId);
        List<ItemRequestDto> itemRequestDtoList = itemRequestRepository
                .findAllByRequestorIdNotLikeOrderByCreatedAsc(userId, PageRequest.of(from, size))
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        itemRequestDtoList.forEach(this::setItemsToItemRequestDto);

        return itemRequestDtoList;
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getById(Long requestId, Long userId) {
        userService.getById(userId);
        ItemRequest itemRequest = itemRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest not found"));
        ItemRequestDto itemRequestDto = toItemRequestDto(itemRequest);
        setItemsToItemRequestDto(itemRequestDto);

        return itemRequestDto;
    }

    private void setItemsToItemRequestDto(ItemRequestDto itemRequestDto) {
        itemRequestDto.setItems(itemService.findAllByRequestId(itemRequestDto.getId()));
    }
}
