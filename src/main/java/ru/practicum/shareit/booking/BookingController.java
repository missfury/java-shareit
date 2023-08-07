package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
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
    public List<BookingDto> getAllBookingByState(@RequestHeader(ITEM_OWNER_ID_HEADER) Long id,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingByState(id, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwnersBookingByState(@RequestHeader(ITEM_OWNER_ID_HEADER) Long id,
                                                       @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllOwnersBookingByState(id, state);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader(ITEM_OWNER_ID_HEADER) Long id,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) {
        return bookingService.approveBooking(id, bookingId, approved);
    }

}

