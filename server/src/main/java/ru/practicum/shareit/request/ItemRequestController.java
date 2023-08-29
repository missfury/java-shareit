package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import java.util.List;

import static ru.practicum.shareit.util.Pagination.getPageOrThrow;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestService requestService;
    public static final String ITEM_OWNER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping()
    public ItemRequestDto addRequest(@RequestHeader(ITEM_OWNER_ID_HEADER) Long id,
                                     @RequestBody ItemRequestShortDto itemRequestShortDto) {
        log.info("Запрос создан");
        return requestService.addRequest(id, itemRequestShortDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(ITEM_OWNER_ID_HEADER) Long id,
                                         @PathVariable Long requestId) {
        log.info("Получен запрос с id = {}", id);
        return requestService.getRequestById(id, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(ITEM_OWNER_ID_HEADER) Long id,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получены все запросы");
        return requestService.getAllRequests(id, getPageOrThrow(from, size));
    }

    @GetMapping()
    public List<ItemRequestDto> getAllRequestsByRequester(@RequestHeader(ITEM_OWNER_ID_HEADER) Long id,
                                                          @RequestParam(defaultValue = "0") Integer from,
                                                          @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получены запросы пользователя ");
        return requestService.getAllRequestsByRequester(id, getPageOrThrow(from, size));
    }

}
