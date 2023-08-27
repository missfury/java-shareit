package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ItemRequestMapperTest {
    private Item item;
    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "John_White", "john@test.com");
        item = new Item(1L, "Item", "Item Description", true, user,
                null);
        itemRequest = new ItemRequest(1L, "Description", user,
                Timestamp.valueOf(LocalDateTime.now()));
    }

    @Test
    void itemRequestToDtoTest() {
        ItemRequestDto itemRequestDto = ItemRequestMapper.itemRequestToDto(itemRequest);

        assertThat(itemRequestDto.getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequestDto.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequestDto.getRequesterId()).isEqualTo(itemRequest.getRequester().getId());
        assertThat(itemRequestDto.getCreated()).isEqualTo(itemRequest.getCreated());
    }

    @Test
    void itemRequestShortToModelTest() {
        ItemRequestShortDto itemRequestShortDto = new ItemRequestShortDto(itemRequest.getId(),
        itemRequest.getDescription());
        ItemRequest itemRequest = ItemRequestMapper.itemRequestShortToModel(itemRequestShortDto);

        assertThat(itemRequest.getDescription()).isEqualTo(itemRequestShortDto.getDescription());
    }

    @Test
    void itemRequestToRequestWithItemsTest() {
        ItemRequestDto itemRequestDto = ItemRequestMapper.itemRequestToRequestWithItems(itemRequest);

        assertThat(itemRequestDto.getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequestDto.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequestDto.getCreated()).isEqualTo(itemRequest.getCreated());
    }

    @Test
    void toItemRequestDtoWithItemsTest() {
        List<ItemDto> itemDtos = new ArrayList<>();
        itemDtos.add(ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build());
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, itemDtos);

        assertThat(itemRequestDto.getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequestDto.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequestDto.getRequesterId()).isEqualTo(itemRequest.getRequester().getId());
        assertThat(itemRequestDto.getCreated()).isEqualTo(itemRequest.getCreated());
        assertThat(itemRequestDto.getItems().get(0).getId()).isEqualTo(item.getId());
        assertThat(itemRequestDto.getItems().get(0).getName()).isEqualTo(item.getName());
        assertThat(itemRequestDto.getItems().get(0).getDescription()).isEqualTo(item.getDescription());
        assertThat(itemRequestDto.getItems().get(0).getAvailable()).isEqualTo(item.getAvailable());
    }
}
