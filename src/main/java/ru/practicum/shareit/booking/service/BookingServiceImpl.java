package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.StatusType.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingDao bookingRepository;

    private final UserService userService;
    private final UserDao userRepository;
    private final ItemService itemService;
    private final ItemDao itemRepository;

    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public BookingDto create(BookingShortDto bookingShortDto, Long userId) {
        userService.getById(userId);
        itemService.getItemById(bookingShortDto.getItemId(), userId);
        Optional<User> user = userRepository.findById(userId);
        Optional<Item> item = itemRepository.findById(bookingShortDto.getItemId());
        if (item.isPresent() && item.get().getOwner().getId().equals(userId)) {
            throw new NotFoundException("User can not book own item");
        }
        if (item.isPresent() && !item.get().getAvailable()) {
            throw new BadRequestException("User can book only available item");
        }
        if (bookingShortDto.getEnd().isBefore(bookingShortDto.getStart())) {
            throw new BadRequestException("End date can not be earlier start date");
        }
        Booking booking = BookingMapper.fromBookingShortDto(bookingShortDto);
        booking.setBooker(user.orElse(null));
        booking.setItem(item.orElse(null));
        booking.setStatus(WAITING);
        bookingRepository.save(booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        userService.getById(userId);
        Booking booking = bookingRepository.findById(bookingId)
                                           .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Only owner can approve or reject");
        }
        if (!booking.getStatus().equals(WAITING)) {
            throw new BadRequestException("Only WAITING can be approved or rejected");
        }
        if (approved) {
            booking.setStatus(APPROVED);
        } else {
            booking.setStatus(REJECTED);
        }
        bookingRepository.save(booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByOwner(Long userId, String state) {
        userService.getById(userId);
        List<Booking> bookingDtoList = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookingDtoList.addAll(bookingRepository.findAllByItemOwnerId(userId, sort));
                break;
            case "CURRENT":
                bookingDtoList.addAll(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), sort));
                break;
            case "PAST":
                bookingDtoList.addAll(bookingRepository.findAllByItemOwnerIdAndEndBefore(userId,
                        LocalDateTime.now(), sort));
                break;
            case "FUTURE":
                bookingDtoList.addAll(bookingRepository.findAllByItemOwnerIdAndStartAfter(userId,
                        LocalDateTime.now(), sort));
                break;
            case "WAITING":
                bookingDtoList.addAll(bookingRepository.findAllByItemOwnerIdAndStatusEquals(userId, WAITING, sort));
                break;
            case "REJECTED":
                bookingDtoList.addAll(bookingRepository.findAllByItemOwnerIdAndStatusEquals(userId, REJECTED, sort));
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookingDtoList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllByUser(Long userId, String state) {
        userService.getById(userId);
        List<Booking> bookingDtoList = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookingDtoList.addAll(bookingRepository.findAllByBookerId(userId, sort));
                break;
            case "CURRENT":
                bookingDtoList.addAll(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), sort));
                break;
            case "PAST":
                bookingDtoList.addAll(bookingRepository.findAllByBookerIdAndEndBefore(userId,
                        LocalDateTime.now(), sort));
                break;
            case "FUTURE":
                bookingDtoList.addAll(bookingRepository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now(), sort));
                break;
            case "WAITING":
                bookingDtoList.addAll(bookingRepository.findAllByBookerIdAndStatusEquals(userId, WAITING, sort));
                break;
            case "REJECTED":
                bookingDtoList.addAll(bookingRepository.findAllByBookerIdAndStatusEquals(userId, REJECTED, sort));
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookingDtoList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                                           .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Booking for specified user not found");
        }

        return BookingMapper.toBookingDto(booking);
    }
}