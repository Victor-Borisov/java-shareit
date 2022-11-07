package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                         .id(booking.getId())
                         .start(booking.getStart())
                         .end(booking.getEnd())
                         .booker(booking.getBooker())
                         .status(booking.getStatus())
                         .item(booking.getItem())
                         .build();
    }

    public static Booking fromBookingDto(BookingDto bookingDto) {
        return Booking.builder()
                      .id(bookingDto.getId())
                      .start(bookingDto.getStart())
                      .end(bookingDto.getEnd())
                      .booker(bookingDto.getBooker())
                      .status(bookingDto.getStatus())
                      .item(bookingDto.getItem())
                      .build();
    }

    public static Booking fromBookingShortDto(BookingShortDto bookingShortDto) {
        return Booking.builder()
                      .id(bookingShortDto.getId())
                      .start(bookingShortDto.getStart())
                      .end(bookingShortDto.getEnd())
                      .build();
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        return BookingShortDto.builder()
                              .id(booking.getId())
                              .bookerId(booking.getBooker().getId())
                              .itemId(booking.getItem().getId())
                              .start(booking.getStart())
                              .end(booking.getEnd())
                              .build();
    }
}
