package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private ItemRequestShort request;

    private Owner owner;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments;

    @Data
    @Builder
    public static class Owner {
        private final long id;
        private final String name;
    }

    @Data
    @Builder
    public static class ItemRequestShort {
        private final long id;
        private final String description;
    }
}
