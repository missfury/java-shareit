package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exception.ValidationException;

import static java.util.Objects.isNull;

@UtilityClass
public class StateValidator {
    public State validateAndGet(final String stateStr) {
        if (isNull(stateStr)) {
            return State.ALL;
        }

        State state;
        try {
            state = State.valueOf(stateStr);
        } catch (IllegalArgumentException exp) {
            throw new ValidationException(String.format("Unknown state: %s", stateStr));
        }

        return state;
    }
}
