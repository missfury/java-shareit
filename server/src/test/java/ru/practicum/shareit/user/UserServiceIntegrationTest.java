package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(properties = {"db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    private final UserService userService;
    private UserDto user1;
    private UserDto user2;
    private UserDto user3;
    private UserDto user4;

    @BeforeEach
    void beforeEach() {
        user1 = UserDto.builder()
                .id(1L)
                .name("User1_name")
                .email("user1@test.com")
                .build();

        user2 = UserDto.builder()
                .id(2L)
                .name("User2_name")
                .email("user2@test.com")
                .build();

        user3 = UserDto.builder()
                .id(3L)
                .name("User3_name")
                .email("user3@test.com")
                .build();

        user4 = UserDto.builder()
                .id(4L)
                .name("User4_name")
                .email("user4@test.com")
                .build();
    }

    @Test
    public void getAllUsersTest() {
        userService.addUser(user1);
        userService.addUser(user2);
        List<UserDto> expectedUsers = List.of(user1, user2);

        List<UserDto> actualUsers = userService.getAllUsers();
        assertEquals(expectedUsers.size(), actualUsers.size());
        assertEquals(expectedUsers.get(0).getName(), actualUsers.get(0).getName());
        assertEquals(expectedUsers.get(0).getEmail(), actualUsers.get(0).getEmail());
    }

    @Test
    public void updateUserTest() {
        userService.addUser(user3);
        user4.setId(3L);
        userService.updateUser(3L, user4);

        UserDto expectedUser = userService.getUserById(3L);

        assertEquals(expectedUser.getName(), user4.getName());
        assertEquals(expectedUser.getEmail(), user4.getEmail());
    }

}
