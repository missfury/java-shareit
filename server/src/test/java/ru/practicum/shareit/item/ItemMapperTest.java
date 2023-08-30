package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ItemMapperTest {
    private Item item;
    private ItemDto itemDto;
    private ItemShortDto itemShortDto;
    private User user;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("John_White")
                .email("john@test.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .owner(user)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .owner(new ItemDto.Owner(user.getId(), user.getName(), user.getEmail()))
                .build();

        itemShortDto = ItemShortDto
                .builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .requestId(1L)
                .build();
    }

    @Test
    void itemToDtoTest() {
        itemDto = ItemMapper.itemToDto(item);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Item name");
        assertThat(itemDto.getDescription()).isEqualTo("Item description");
        assertThat(itemDto.getAvailable()).isEqualTo(true);
        assertThat(itemDto.getComments()).isEqualTo(List.of());
        assertThat(itemDto.getRequestId()).isEqualTo(null);

    }

    @Test
    void itemToModelTest() {

        item = ItemMapper.itemToModel(itemDto);

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("Item name");
        assertThat(item.getDescription()).isEqualTo("Item description");
        assertThat(item.getAvailable()).isEqualTo(true);
        assertThat(item.getOwner().getId()).isEqualTo(user.getId());
        assertThat(item.getOwner().getName()).isEqualTo(user.getName());
        assertThat(item.getOwner().getEmail()).isEqualTo(user.getEmail());
        assertThat(item.getRequest()).isEqualTo(null);
    }

    @Test
    void itemToShortDtoTest() {
        itemDto = ItemMapper.itemToShortDto(itemShortDto);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(this.itemDto.getName()).isEqualTo("Item name");
        assertThat(this.itemDto.getDescription()).isEqualTo("Item description");
        assertThat(itemDto.getAvailable()).isEqualTo(true);
        assertThat(itemDto.getComments()).isEqualTo(List.of());
    }
}
