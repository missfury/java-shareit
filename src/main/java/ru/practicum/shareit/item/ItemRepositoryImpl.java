package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long lastItemId;

    @Override
    public Item addItem(Item item) {
        long itemId = ++lastItemId;
        item.setId(itemId);
        items.put(itemId, item);
        log.info("Предмет с id: {} добавлен", itemId);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        log.info("Информация о предмете с id: {} обновлена", item.getId());
        return item;
    }

    @Override
    public void deleteItemById(long itemId) {
        items.remove(itemId);
        log.info("Предмет с id: {} удален", itemId);
    }

    @Override
    public Item findItemById(long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> findItemsByUserId(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findItemsByText(String text) {
        return items.values().stream()
                .filter(item ->
                        (item.getName().toLowerCase().contains(text.toLowerCase()))
                                || (item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkItemExist(long id) {
        return items.containsKey(id);
    }
}