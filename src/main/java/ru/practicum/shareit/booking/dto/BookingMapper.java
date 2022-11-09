package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;

@UtilityClass
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        BookingDto.Booker booker = BookingDto.Booker.builder()
                                                    .id(booking.getBooker().getId())
                                                    .name(booking.getBooker().getName())
                                                    .build();
        BookingDto.BookedItem bookedItem = BookingDto.BookedItem.builder()
                                                                .id(booking.getItem().getId())
                                                                .name(booking.getItem().getName())
                                                                .build();
        return BookingDto.builder()
                         .id(booking.getId())
                         .start(booking.getStart())
                         .end(booking.getEnd())
                         .booker(booker)
                         .status(booking.getStatus())
                         .item(bookedItem)
                         .bookerId(booking.getBooker().getId())
                         .build();
    }

    public static Booking fromBookingShortDto(BookingRequestDto bookingRequestDto) {
        return Booking.builder()
                      .id(bookingRequestDto.getId())
                      .start(bookingRequestDto.getStart())
                      .end(bookingRequestDto.getEnd())
                      .build();
    }

}
