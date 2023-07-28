package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item addItem(Item item);

    Item updateItem(Item item);

    void deleteItemById(long itemId);

    Item findItemById(long itemId);

    List<Item> findItemsByUserId(long userId);

    List<Item> findItemsByText(String text);

    boolean checkItemExist(long itemId);
}
