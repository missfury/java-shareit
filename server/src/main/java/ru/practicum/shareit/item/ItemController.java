package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.util.Pagination.getPageOrThrow;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;
    public static final String ITEM_OWNER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemShortDto itemShortDto,
                           @RequestHeader(ITEM_OWNER_ID_HEADER) Long userId) {
        return service.addItem(itemShortDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId,
                               @RequestHeader(ITEM_OWNER_ID_HEADER) Long userId) {
        return service.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(ITEM_OWNER_ID_HEADER) Long userId,
                                     @RequestParam(name = "from", required = false) Integer from,
                                     @RequestParam(name = "size", required = false) Integer size) {
        return service.getAllItems(userId, getPageOrThrow(from, size));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemShortDto itemShortDto,
                              @PathVariable Long itemId,
                              @RequestHeader(ITEM_OWNER_ID_HEADER) Long userId) {
        return service.updateItem(itemShortDto, itemId, userId);

    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByText(@RequestParam String text,
                                          @RequestParam(name = "from", required = false) Integer from,
                                          @RequestParam(name = "size", required = false) Integer size) {
        return service.searchItemByText(text, getPageOrThrow(from, size));
    }


    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestHeader(ITEM_OWNER_ID_HEADER) Long userId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return service.addComment(commentDto, userId, itemId);
    }
}






