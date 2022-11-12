package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.StatusType;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingRequestDto bookingRequestDto, Long userId);

    BookingDto approve(Long bookingId, Long userId, Boolean approved);

    List<BookingDto> getAllByOwner(Long userId, StatusType state);

    List<BookingDto> getAllByUser(Long userId, StatusType state);

    BookingDto getById(Long itemId, Long userId);
}
