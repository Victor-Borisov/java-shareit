package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;

    @NotBlank(groups = {Marker.OnCreate.class})
    private String name;

    @NotBlank(groups = {Marker.OnCreate.class})
    private String description;

    @NotNull(groups = {Marker.OnCreate.class})
    private Boolean available;

    private ItemRequest request;

    private User owner;

    private BookingShortDto lastBooking;

    private BookingShortDto nextBooking;

    private List<CommentDto> comments;
}
