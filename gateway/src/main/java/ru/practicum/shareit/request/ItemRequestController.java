package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    public static final String ITEM_OWNER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader(ITEM_OWNER_ID_HEADER) Long userId,
                                                 @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Create ite, request {}", userId);
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUser(@RequestHeader(ITEM_OWNER_ID_HEADER) Long userId) {
        log.info("Get all  for user with id =  {}", userId);
        return itemRequestClient.getItemRequestsByUser(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(ITEM_OWNER_ID_HEADER) Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Get request with id = {}", requestId);
        return itemRequestClient.getItemRequest(requestId, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(ITEM_OWNER_ID_HEADER) Long userId,
                                                     @PositiveOrZero
                                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive
                                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all item requests for user with id =  {}", userId);
        return itemRequestClient.getAllItemRequest(userId, from, size);
    }


}
