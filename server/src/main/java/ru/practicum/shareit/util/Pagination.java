package ru.practicum.shareit.util;

import ru.practicum.shareit.exception.NotAvailableException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class Pagination {
    public static Pageable getPageOrThrow(Integer from, Integer size) {
        if (size == null || from == null) {
            return Pageable.unpaged();
        }
        if (size <= 0 || from < 0) {
            throw new NotAvailableException("incorrect page parameters");
        }
        from = from / size;
        return PageRequest.of(from, size);
    }
}
