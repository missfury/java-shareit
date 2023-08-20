package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.request.ItemRequestMapper.itemRequestToShortDto;
import static ru.practicum.shareit.user.UserMapper.userToModel;


@Transactional
@SpringBootTest(properties = {"db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private ItemDto itemDto;
    private ItemShortDto itemShortDto;
    private UserDto userOwner;
    private UserDto userRequestor;
    private UserDto savedRequestor;
    private ItemRequestDto savedRequest;

    @BeforeEach
    void beforeEach() {
        userOwner = UserDto.builder()
                .name("John_White")
                .email("john@test.com")
                .build();
        userRequestor = UserDto.builder()
                .name("Mary_Sue")
                .email("mary@test.com")
                .build();
        itemDto = ItemDto.builder()
                .name("Item")
                .description("Item description")
                .available(true)
                .owner(new ItemDto.Owner(userOwner.getId(), userOwner.getName(), userOwner.getEmail()))
                .build();
        itemShortDto = ItemShortDto.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .requestId(itemDto.getRequestId())
                .available(itemDto.getAvailable())
                .build();
    }

    @Test
    public void getAllItemRequestsTest() {
        savedRequestor = userService.addUser(userRequestor);
        ItemRequest addItemRequestDto = ItemRequest.builder()
                .description("Description")
                .requester(userToModel(savedRequestor))
                .created(Timestamp.valueOf(LocalDateTime.now()))
                .build();
        savedRequest = itemRequestService.addRequest(savedRequestor.getId(), itemRequestToShortDto(addItemRequestDto));
        List<ItemRequestDto> requestsList = itemRequestService.getAllRequestsByRequester(savedRequestor.getId(),
        0, 10);

        assertEquals(1, requestsList.size());
        assertEquals(savedRequest.getId(), requestsList.get(0).getId());
        assertEquals(savedRequest.getDescription(), requestsList.get(0).getDescription());
    }

}
