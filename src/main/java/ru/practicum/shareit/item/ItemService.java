package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotExistException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemDto addItem(long userId, ItemDto itemDto) {
        userService.throwIfUserNotExist(userId);
        Item item = ItemMapper.toModel(itemDto);
        item.setOwnerId(userId);
        return ItemMapper.toDto(itemRepository.addItem(item));
    }

    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        userService.throwIfUserNotExist(userId);
        throwIfItemNotExist(itemId);
        if (itemRepository.findItemById(itemId).getOwnerId() != userId)
            throw new NotExistException("Предмет с id " + itemId + " не принадлежит пользователю с id " + userId);
        return ItemMapper.toDto(itemRepository.updateItem(updateItemInfo(itemId, itemDto)));
    }

    public void deleteItem(long userId, long itemId) {
        userService.throwIfUserNotExist(userId);
        throwIfItemNotExist(itemId);
        itemRepository.deleteItemById(itemId);
    }

    public ItemDto getItemById(long itemId) {
        throwIfItemNotExist(itemId);
        return ItemMapper.toDto(itemRepository.findItemById(itemId));
    }

    public List<ItemDto> getItemsByUserId(long userId) {
        userService.throwIfUserNotExist(userId);
        return itemRepository.findItemsByUserId(userId).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItemsByText(String text) {
        if (text.isEmpty())
            return List.of();
        return itemRepository.findItemsByText(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    private Item updateItemInfo(long id, ItemDto itemDto) {
        Item itemInfo = ItemMapper.toModel(itemDto);
        Item oldItemInfo = itemRepository.findItemById(id);
        if (itemInfo.getName() == null)
            itemInfo.setName(oldItemInfo.getName());
        if (itemInfo.getDescription() == null)
            itemInfo.setDescription(oldItemInfo.getDescription());
        if (itemInfo.getAvailable() == null)
            itemInfo.setAvailable(oldItemInfo.getAvailable());
        itemInfo.setId(id);
        itemInfo.setOwnerId(oldItemInfo.getOwnerId());
        return itemInfo;
    }

    private void throwIfItemNotExist(long id) {
        if (!itemRepository.checkItemExist(id))
            throw new NotExistException("Предмет с id " + id + " не найден");
    }
}
