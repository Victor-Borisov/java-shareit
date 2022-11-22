package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.StatusType;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private StatusType status;

    private Booker booker;

    private BookedItem item;

    private Long bookerId;

    @Data
    @Builder
    public static class Booker {
        private final long id;
        private final String name;
    }

    @Data
    @Builder
    public static class BookedItem {
        private final long id;
        private final String name;
    }
}
