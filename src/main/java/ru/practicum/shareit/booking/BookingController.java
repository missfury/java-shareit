package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;
    public static final String ITEM_OWNER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping()
    public BookingDto addBooking(@RequestHeader(ITEM_OWNER_ID_HEADER) Long id,
                                 @Valid @RequestBody BookingShortDto bookingShortDto) {
        return bookingService.addBooking(id, bookingShortDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(ITEM_OWNER_ID_HEADER) Long id,
                                     @PathVariable Long bookingId) {
        return bookingService.getBooking(id, bookingId);
    }

    @GetMapping()
    public List<BookingDto> getAllBookingByState(@RequestHeader("X-Sharer-User-Id") Long id,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getAllBookingByState(id, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwnersBookingByState(@RequestHeader("X-Sharer-User-Id") Long id,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getAllOwnersBookingByState(id, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader(ITEM_OWNER_ID_HEADER) Long id,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) {
        return bookingService.approveBooking(id, bookingId, approved);
    }

}

