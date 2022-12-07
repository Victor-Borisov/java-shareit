package ru.practicum.shareit.booking.dao;

import ru.practicum.shareit.booking.dto.LastNextBookingDto;

import java.util.List;

public interface BookingCustomDao {
    List<LastNextBookingDto> findLastNextBooking(List<Long> items);
}
