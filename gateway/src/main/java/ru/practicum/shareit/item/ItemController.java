package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    public static final String ITEM_OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId,
                                             @Valid @RequestBody ItemDto itemGateDto) {
        log.info("Create item {}, userId={}", itemGateDto, userId);
        return itemClient.addItem(userId, itemGateDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemDto itemGateDto) {
        log.info("Update item {}, userId={}, itemId={}", itemGateDto, userId, itemId);
        return itemClient.updateItem(userId, itemId, itemGateDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId,
                           @PathVariable long itemId) {
        log.info("Delete item with id={}, from userId={}", itemId, userId);
        itemClient.deleteItem(userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId,
                                          @PathVariable long itemId) {
        log.info("Get item with id={}, from userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader(ITEM_OWNER_ID_HEADER)long userId,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0", required = false)
                                                   Integer from,
                                                   @Positive @RequestParam(defaultValue = "10", required = false)
                                                   Integer size) {
        log.info("Get items from owner with id: {}", userId);
        return itemClient.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @PositiveOrZero @RequestParam(defaultValue = "0", required = false)
                                              Integer from,
                                              @Positive @RequestParam(defaultValue = "10", required = false)
                                                  Integer size) {
        log.info("Search items with text={}", text);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId,
                                                @PathVariable long itemId,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.info("Create comment = {} to item with id: {} from user with id: {}", commentDto, itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
