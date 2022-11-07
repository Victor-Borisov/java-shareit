package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.CommentDao;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
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
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        List<ItemDto> itemDtoList = itemRepository.findAllByOwnerId(ownerId).stream()
                                                  .map(ItemMapper::toItemDto).collect(Collectors.toList());
        itemDtoList.forEach(itemDto -> {
            itemDto.setLastBooking(bookingRepository.findAllByItemIdOrderByStartAsc(itemDto.getId()).isEmpty() ?
                    null : BookingMapper
                    .toBookingShortDto(bookingRepository.findAllByItemIdOrderByStartAsc(itemDto.getId()).get(0)));
            itemDto.setNextBooking(itemDto.getLastBooking() == null ?
                    null : BookingMapper
                    .toBookingShortDto(bookingRepository.findAllByItemIdOrderByStartDesc(itemDto.getId()).get(0)));
            itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId())
                                                 .stream()
                                                 .map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        });

        return itemDtoList;
    }

    @Override
    public ItemDto getItemById(Long itemId, Long ownerId) {
        Item item = itemRepository.findById(itemId)
                                  .orElseThrow(() -> new NotFoundException("Item not found"));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(commentRepository.findAllByItemId(itemId).stream()
                                             .map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        if (item.getOwner().getId().equals(ownerId)) {
            itemDto.setLastBooking(bookingRepository.findAllByItemIdOrderByStartAsc(itemId).isEmpty() ? null :
                    BookingMapper.toBookingShortDto(bookingRepository.findAllByItemIdOrderByStartAsc(itemId).get(0)));
            itemDto.setNextBooking(itemDto.getLastBooking() == null ? null :
                    BookingMapper.toBookingShortDto(bookingRepository.findAllByItemIdOrderByStartDesc(itemDto.getId())
                                                                     .get(0)));
        }

            return itemDto;
        }

        @Override
        public List<ItemDto> searchItems(String searchedText) {
            if (searchedText.isEmpty() || searchedText.isBlank()) {
                return Collections.emptyList();
            }
            List<Item> allItems = itemRepository.findAllByCriteria(searchedText);
            return allItems.stream()
                           .map(ItemMapper::toItemDto)
                           .collect(Collectors.toList());
        }

        @Override
        public ItemDto createItem(ItemDto itemDto, Long ownerId) {
            User owner = UserMapper.fromUserDto(userService.getById(ownerId));
            Item item = ItemMapper.fromItemDto(itemDto, owner);
            item.setOwner(owner);

            return ItemMapper.toItemDto(itemRepository.save(item));
        }

        @Override
        public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
            Item item = itemRepository.findById(itemId)
                                      .orElseThrow(() -> new NotFoundException("Item not found"));
            if (!item.getOwner().getId().equals(userId)) {
                throw new ForbiddenException("Only owner can edit the Item");
            }
            Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
            Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
            Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setAvailable);

            return ItemMapper.toItemDto(itemRepository.save(item));
        }

        @Override
        public ItemDto deleteItem(Long itemId, Long userId) {
            ItemDto itemDto = getItemById(itemId, userId);
            itemRepository.deleteById(itemId);

            return itemDto;
        }

        @Override
        public CommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {
            User user = UserMapper.fromUserDto(userService.getById(userId));
            ItemDto itemDto = getItemById(itemId, userId);
            Item item = ItemMapper.fromItemDto(itemDto, user);
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
