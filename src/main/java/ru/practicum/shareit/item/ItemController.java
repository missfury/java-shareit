package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private static final String ITEM_OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService service;

    @PostMapping
    public ItemDto addItem(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info(String.format("POST /items, body = %s, %s = %s", itemDto, ITEM_OWNER_ID_HEADER, userId));
        final ItemDto item =  service.addItem(userId, itemDto);
        log.info(String.format("Успешно добавлен предмет с id = %s", item.getId()));
        return item;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info(String.format("PATCH /items/{itemId}, body = %s, {itemId} = %s, %s = %s", itemDto, itemId,
        ITEM_OWNER_ID_HEADER, userId));
        final ItemDto item = service.updateItem(userId, itemId, itemDto);
        log.info(String.format("Успешно обновлена информация о предмете с id = %s", item.getId()));
        return item;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId,
                           @PathVariable long itemId) {
        log.info(String.format("DELETE /items/{itemId}, {itemId} = %s, %s = %s", itemId, ITEM_OWNER_ID_HEADER, userId));
        service.deleteItem(userId, itemId);
        log.info(String.format("Предмет с id = %s успешно удален", itemId));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId) {
        log.info(String.format("GET /items/{itemId}, {itemId} = %s", itemId));
        final ItemDto item = service.getItemById(itemId);
        log.info(String.format("Получена информация о предмете с id = %s", item.getId()));
        return item;
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader(ITEM_OWNER_ID_HEADER) long userId) {
        log.info(String.format("GET /items, %s = %s", ITEM_OWNER_ID_HEADER, userId));
        final List<ItemDto> items = service.getItemsByUserId(userId);
        log.info(String.format("Успешно получены предметы (%s шт.) пользователя с id = %s", items.size(), userId));
        return items;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info(String.format("GET /items/search, text = %s", text));
        List<ItemDto> searchResults = service.searchItemsByText(text);
        log.info(String.format("Получено %d результатов поиска по тексту '%s'", searchResults.size(), text));
        return searchResults;
    }
}
