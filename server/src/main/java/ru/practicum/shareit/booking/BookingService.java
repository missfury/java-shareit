package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long id, BookingShortDto bookingShortDto);

    BookingDto getBooking(Long id, Long bookingId);

    List<BookingDto> getAllBookingByState(Long id, String state, Pageable page);

    BookingDto approveBooking(Long id, Long bookingId, Boolean approved);

    List<BookingDto> getAllOwnersBookingByState(Long id, String state, Pageable page);
}
