package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.item.ItemMapper.itemToModel;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemService service;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private ItemDto itemDtoOne;
    private ItemDto itemDtoAnother;
    private ItemShortDto itemShortDto;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("John_White")
                .email("john@test.com")
                .build();

        itemDtoOne = ItemDto.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .owner(new ItemDto.Owner(user.getId(), user.getName(), user.getEmail()))
                .build();

        itemDtoAnother = ItemDto.builder()
                .id(2L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .owner(new ItemDto.Owner(user.getId(), user.getName(), user.getEmail()))
                .build();

        itemShortDto = ItemShortDto.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .requestId(user.getId())
                .build();

    }

    @Test
    void getAllItemsByOwnerIdTest() {
        userRepository.save(user);
        itemRepository.saveAll(List.of(itemToModel(itemDtoOne), itemToModel(itemDtoAnother)));

        long userId = user.getId();
        int from = 0;
        int size = 10;
        List<ItemDto> itemDtoList =
                service.getAllItems(userId, PageRequest.of(from, size));

        assertEquals(2, itemDtoList.size(), "Item number is not correct");
        assertEquals(itemDtoOne.getId(), itemDtoList.get(0).getId());
        assertEquals(itemDtoAnother.getId(), itemDtoList.get(1).getId());
        assertEquals(itemDtoOne.getName(), itemDtoList.get(0).getName());
        assertEquals(itemDtoAnother.getName(), itemDtoList.get(1).getName());
    }

    @Test
    void getItemByIdTest() {
        userRepository.save(user);
        Item savedItem = itemRepository.save(itemToModel(itemDtoOne));

        long userId = user.getId();
        long itemId = savedItem.getId();
        ItemDto itemById = service.getItemById(userId, itemId);

        assertEquals(itemDtoOne.getName(), itemById.getName());
    }

    @Test
    void getItemByIdWithWrongArgsTest() {
        userRepository.save(user);
        Item savedItem = itemRepository.save(itemToModel(itemDtoOne));

        assertThrows(NotFoundException.class, () -> service.getItemById(9999L, savedItem.getId()));
    }

    @Test
    void createNewItemTest() {
        userRepository.save(user);

        ItemDto item = service.addItem(new ItemShortDto(itemDtoOne.getId(), itemDtoOne.getName(),
                itemDtoOne.getDescription(), true, null), user.getId());

        assertNotNull(item.getId());
        assertEquals(itemDtoOne.getName(), item.getName());
        assertEquals(itemDtoOne.getDescription(), item.getDescription());
    }

    @Test
    void createNewItemWithIncorrectUserTest() {
        assertThrows(NotFoundException.class, () -> service.addItem(itemShortDto, 9999L));
    }
}