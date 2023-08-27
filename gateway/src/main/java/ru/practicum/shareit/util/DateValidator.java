package ru.practicum.shareit.util;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

public class DateValidator {
    public static void validate(LocalDateTime start, LocalDateTime end) {
        if (start.equals(end)) {
            throw new ValidationException("Дата начала бронирования не может быть равна дате конца бронирования!");
        }

        if (end.isBefore(start)) {
            throw new ValidationException("Дата конца бронирования не может быть раньше даты начала бронирования!");
        }
    }

    public static void validate(BookItemRequestDto bookingDto) {
        validate(bookingDto.getStart(), bookingDto.getEnd());
    }
}
