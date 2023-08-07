package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.NotSupportedStateException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State validateState(String state) {
        try {
            return State.valueOf(state.toUpperCase());
        } catch (RuntimeException e) {
            throw new NotSupportedStateException("Unknown state: " + state);
        }
    }

}