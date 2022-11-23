package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

    private OwnerTiny owner;

    private BookingTiny lastBooking;

    private BookingTiny nextBooking;

    private List<CommentDto> comments;

    @Data
    @Builder
    public static class OwnerTiny {
        private final long id;
        private final String name;
    }

    @Data
    @Builder
    public static class BookingTiny {
        private Long id;
        private Long bookerId;
    }
}
