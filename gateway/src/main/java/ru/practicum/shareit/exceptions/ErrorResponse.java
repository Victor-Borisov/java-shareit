package ru.practicum.shareit.exceptions;

import lombok.Data;
import lombok.NonNull;

@Data
public class ErrorResponse {
    @NonNull
    private String error;
    private String description;
}
