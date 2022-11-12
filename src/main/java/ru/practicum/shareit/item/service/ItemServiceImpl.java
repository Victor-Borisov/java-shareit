package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.StatusType.APPROVED;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemRepository;
    private final CommentDao commentRepository;
    private final UserService userService;

    private final BookingDao bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getByOwner(Long ownerId) {
        List<ItemDto> itemDtoList = itemRepository.findAllByOwnerId(ownerId).stream()
                                                  .map(ItemMapper::toItemDto).collect(Collectors.toList());
        List<Long> items = itemDtoList.stream().map(ItemDto::getId).collect(Collectors.toList());
        List<LastNextBookingDto> findLastNextBooking = bookingRepository.findLastNextBooking(items);
        itemDtoList.forEach(itemDto -> {
            LastNextBookingDto lastNextBookingDto = findLastNextBooking
                    .stream()
                    .filter(o -> o.getItemId().equals(itemDto.getId()))
                    .collect(Collectors.toList()).get(0);

            ItemDto.BookingTiny lastBooking = ItemDto.BookingTiny
                    .builder()
                    .id(lastNextBookingDto.getLastBookingId())
                    .bookerId(lastNextBookingDto.getLastBookingBookerId())
                    .build();
            ItemDto.BookingTiny nextBooking = ItemDto.BookingTiny
                    .builder()
                    .id(lastNextBookingDto.getNextBookingId())
                    .bookerId(lastNextBookingDto.getNextBookingBookerId())
                    .build();
            itemDto.setLastBooking(lastBooking.getId() == 0 ? null : lastBooking);
            itemDto.setNextBooking(nextBooking.getId() == 0 ? null : nextBooking);

            itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId())
                                                 .stream()
                                                 .map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        });

        return itemDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(Long itemId, Long ownerId) {
        Item item = itemRepository.findById(itemId)
                                  .orElseThrow(() -> new NotFoundException("Item not found"));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(commentRepository.findAllByItemId(itemId).stream()
                                             .map(CommentMapper::toCommentDto).collect(Collectors.toList()));
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
        public List<ItemDto> search(String searchedText) {
            if (searchedText.isEmpty() || searchedText.isBlank()) {
                return Collections.emptyList();
            }
            List<Item> allItems = itemRepository.findAllByCriteria(searchedText);
            return allItems.stream()
                           .map(ItemMapper::toItemDto)
                           .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public ItemDto create(ItemRequestDto itemDto, Long ownerId) {
            User owner = UserMapper.fromUserDto(userService.getById(ownerId));
            Item item = ItemMapper.fromItemRequestDto(itemDto);
            item.setOwner(owner);

            return ItemMapper.toItemDto(itemRepository.save(item));
        }

        @Override
        @Transactional
        public ItemDto update(ItemRequestDto itemDto, Long itemId, Long userId) {
            Item item = itemRepository.findById(itemId)
                                      .orElseThrow(() -> new NotFoundException("Item not found"));
            if (!item.getOwner().getId().equals(userId)) {
                throw new ForbiddenException("Only owner can edit the Item");
            }
            Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
            Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
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
    }
