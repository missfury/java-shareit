package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exception.UnsupportedStatusException;


import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    public static final String ITEM_OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId,
                                                @RequestBody @Valid BookItemRequestDto bookingGateDto) {
        log.info("Create booking {}, userId={}", bookingGateDto, userId);
        return bookingClient.addBooking(userId, bookingGateDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId,
                                                  @PathVariable long bookingId, @RequestParam boolean approved) {
        log.info("Approve bookingId={}, from userId={}, approved={}", bookingId, userId, approved);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Get booking with id={}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                  Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10")
                                                  Integer size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));
        log.info("Get bookings by bookerId={}, state={}, from={}, size={}", userId, stateParam, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId,
                                                     @RequestParam(name = "state", defaultValue = "ALL")
                                                     String stateParam,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0", required = false)
                                                     Integer from,
                                                     @Positive @RequestParam(defaultValue = "10", required = false)
                                                     Integer size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));
        log.info("Get bookings by ownerId={}, state={}, from={}, size={}", userId, stateParam, from, size);
        return bookingClient.getBookingsByOwner(userId, state, size, from);
    }

}