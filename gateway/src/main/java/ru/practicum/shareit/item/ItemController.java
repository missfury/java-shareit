package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@Controller
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;
    public static final String ITEM_OWNER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(ITEM_OWNER_ID_HEADER) Long userId,
                                          @Valid @RequestBody ItemDto requestDto) {
        log.info("Create item");
        return itemClient.addItem(userId, requestDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable Long id,
                                              @RequestHeader(ITEM_OWNER_ID_HEADER) Long userId) {
        log.info("Get item with id = {}", id);
        return itemClient.getItemById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(ITEM_OWNER_ID_HEADER) Long userId) {
        log.info("Get items {}", userId);
        return itemClient.getItems(userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto requestDto,
                                             @PathVariable Long id,
                                             @RequestHeader(ITEM_OWNER_ID_HEADER) Long userId) {
        log.info("Update item with id = {}", id);
        return itemClient.updateItem(requestDto, id, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByText(@RequestParam String text,
                                                   @RequestHeader(ITEM_OWNER_ID_HEADER) Long userId) {
        log.info("Search item by text {} ", text);
        return itemClient.searchItemByText(userId, text);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> removeItem(@PathVariable Long id) {
        log.info("Remove item with id = {}", id);
        return itemClient.removeItem(id);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                                @RequestHeader(ITEM_OWNER_ID_HEADER) Long userId,
                                                @RequestBody CommentDto requestDto) {
        log.info("Create comment for item with id = {}", itemId);
        return itemClient.addComment(itemId, userId, requestDto);
    }
}
