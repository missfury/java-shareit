package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemShortDto itemShortDto, Long itemId);

    List<ItemDto> getAllItems(Long userId, Pageable page);

    ItemDto getItemById(Long itemId, Long userId);

    ItemDto updateItem(ItemShortDto itemShortDto, Long itemId, Long userId);

    List<ItemDto> searchItemByText(String text, Pageable page);

    CommentDto addComment(CommentDto commentDto, Long userId, Long itemId);
}
