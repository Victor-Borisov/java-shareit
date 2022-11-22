package ru.practicum.shareit.booking;

import ru.practicum.shareit.exceptions.BadRequestException;

public enum StatusType {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    APPROVED,
    REJECTED,

    CANCELED;

    public static StatusType getEnumByString(String value) {
        StatusType statusType;
        try {
            statusType = StatusType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + value);
        }
        return statusType;
    }
}
