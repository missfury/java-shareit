package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.util.BookingDatesValidator;
import ru.practicum.shareit.util.StateValidator;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;
    public static final String ITEM_OWNER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId,
                                                @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating a booking {}, userId={}", requestDto, userId);
        BookingDatesValidator.validate(requestDto);
        return bookingClient.addBooking(userId, requestDto);
    }


    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId,
                                         @RequestParam(defaultValue = "all") String state,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size) {

        final String logStr = "GET /bookings?state={state}&from={from}&size={size}, {state} = %s, " +
                "{from} = %s, {size} = %s, %s = %s";

        log.info(String.format(logStr, state, from, size, ITEM_OWNER_ID_HEADER, userId));
        final State states = StateValidator.validateAndGet(state);
        return bookingClient.getBookings(userId, states, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwner(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId,
                                           @RequestParam(defaultValue = "all") String state,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {

        final String logStr = "GET /bookings/owner?state={state}&from={from}&size={size}, " +
                "{state} = %s, {from} = %s, {size} = %s, %s = %s";
        log.info(String.format(logStr, state, from, size, ITEM_OWNER_ID_HEADER, userId));

        final State states = StateValidator.validateAndGet(state);
        return bookingClient.getBookingByOwner(userId, states, from, size);
    }


    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {} of userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam boolean approved) {
        log.info("Approve booking with id =  {}", bookingId);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }
}