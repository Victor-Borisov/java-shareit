package ru.practicum.shareit.booking.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LastNextBookingDto {
    private Long itemId;
    private Long lastBookingId;
    private Long lastBookingBookerId;
    private Long nextBookingId;
    private Long nextBookingBookerId;
}
