package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.StatusType;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.LastNextBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.CommentDao;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static ru.practicum.shareit.booking.StatusType.APPROVED;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemRepository;

    private final ItemRequestDao itemRequestRepository;
    private final CommentDao commentRepository;
    private final UserService userService;

    private final BookingDao bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getByOwner(Long ownerId, int from, int size) {
        List<Item> itemList = itemRepository.findAllByOwnerId(ownerId, PageRequest.of(from, size));
        List<Long> items = itemList.stream().map(Item::getId).collect(toList());//needs to select bookings
        List<LastNextBookingDto> findLastNextBooking = bookingRepository.findLastNextBooking(items);
        Map<Item, List<Comment>> comments = commentRepository.findAllByItemIn(itemList,
                Sort.by(DESC, "created")).stream().collect(groupingBy(Comment::getItem, toList()));
        return itemList.stream().map(item -> {
                    ItemDto itemDto = ItemMapper.toItemDto(item);
                    LastNextBookingDto lastNextBookingDto =
                            findLastNextBooking.stream()
                                               .filter(o -> o.getItemId().equals(itemDto.getId()))
                                               .collect(toList()).get(0);
                    ItemDto.BookingTiny lastBooking =
                            ItemDto.BookingTiny.builder()
                                               .id(lastNextBookingDto.getLastBookingId())
                                               .bookerId(lastNextBookingDto.getLastBookingBookerId()).build();
                    ItemDto.BookingTiny nextBooking =
                            ItemDto.BookingTiny.builder()
                                               .id(lastNextBookingDto.getNextBookingId())
                                               .bookerId(lastNextBookingDto.getNextBookingBookerId()).build();
                    itemDto.setLastBooking(lastBooking.getId() == 0 ? null : lastBooking);
                    itemDto.setNextBooking(nextBooking.getId() == 0 ? null : nextBooking);
                    List<Comment> commentList = comments.get(item);
                    itemDto.setComments(commentList == null ? null : commentList
                            .stream().map(CommentMapper::toCommentDto).collect(toList()));
                    return itemDto;
                }).collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(Long itemId, Long ownerId) {
        Item item = itemRepository.findById(itemId)
                                  .orElseThrow(() -> new NotFoundException("Item not found"));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(commentRepository.findAllByItemId(itemId).stream()
                                             .map(CommentMapper::toCommentDto).collect(toList()));
        if (item.getOwner().getId().equals(ownerId)) {
            Booking lastBooking =
                    bookingRepository.findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(itemId,
                            LocalDateTime.now(), List.of(StatusType.APPROVED)).orElse(null);
            Booking nextBooking =
                    bookingRepository.findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(itemId,
                            LocalDateTime.now(), List.of(StatusType.APPROVED)).orElse(null);
            itemDto.setLastBooking(lastBooking == null ? null : BookingMapper.toBookingTiny(lastBooking));
            itemDto.setNextBooking(nextBooking == null ? null : BookingMapper.toBookingTiny(nextBooking));
        }

        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String searchedText, int from, int size) {
        if (searchedText.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> allItems = itemRepository.findAllByCriteria(searchedText, PageRequest.of(from, size));
        return allItems.stream()
                       .map(ItemMapper::toItemDto)
                       .collect(toList());
    }

    @Override
    @Transactional
    public ItemDto create(ItemInDto itemDto, Long ownerId) {
        User owner = UserMapper.fromUserDto(userService.getById(ownerId));
        Item item = ItemMapper.fromItemInDto(itemDto);
        ItemRequest itemRequest = itemDto.getRequestId() == null ? null : itemRequestRepository
                .findById(itemDto.getRequestId())
                .orElseThrow(() -> new NotFoundException("ItemRequest not found"));
        item.setOwner(owner);
        item.setRequest(itemRequest);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(ItemInDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                                  .orElseThrow(() -> new NotFoundException("Item not found"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only owner can edit the Item");
        }
        if (!StringUtils.isBlank(itemDto.getName())) {
            item.setName(itemDto.getName());
        }
        if (!StringUtils.isBlank(itemDto.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setAvailable);

        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto delete(Long itemId, Long userId) {
        ItemDto itemDto = getById(itemId, userId);
        itemRepository.deleteById(itemId);

        return itemDto;
    }

    @Override
    @Transactional
    public CommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = UserMapper.fromUserDto(userService.getById(userId));
        Item item = itemRepository.findById(itemId)
                                  .orElseThrow(() -> new NotFoundException("Item not found"));
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId, APPROVED,
                LocalDateTime.now()).isEmpty()) {
            throw new BadRequestException("Comment can be added only after booking");
        }
        Comment comment = CommentMapper.fromCommentDto(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> findAllByRequestId(Long requestId) {
        List<Item> allItems = itemRepository.findAllByRequestId(requestId);
        return allItems.stream()
                       .map(ItemMapper::toItemDto)
                       .collect(toList());
    }
}
