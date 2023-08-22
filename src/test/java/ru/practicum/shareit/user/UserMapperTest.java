package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserMapperTest {

    private User user;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "John_White", "john@test.com");
        userDto = new UserDto(2L, "Mary_Sue", "mary@test.com");
    }

    @Test
    void userToDtoTest() {
        UserDto userDto = UserMapper.userToDto(user);

        assertThat(userDto.getId()).isEqualTo(user.getId());
        assertThat(userDto.getName()).isEqualTo(user.getName());
        assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void userToModelTest() {
        User user = UserMapper.userToModel(userDto);

        assertThat(user.getId()).isEqualTo(userDto.getId());
        assertThat(user.getName()).isEqualTo(userDto.getName());
        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    void userToOwnerTest() {
        ItemDto.Owner owner = UserMapper.userToOwner(userDto);

        assertThat(owner.getId()).isEqualTo(userDto.getId());
        assertThat(owner.getName()).isEqualTo(userDto.getName());
        assertThat(owner.getEmail()).isEqualTo(userDto.getEmail());
    }

}