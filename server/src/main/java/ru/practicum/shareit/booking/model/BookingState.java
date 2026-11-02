package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.model.BadRequestException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState from(String state) {
        if (state == null || state.isBlank()) {
            return ALL;
        }
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Неизвестный параметр: " + state);
        }
    }
}