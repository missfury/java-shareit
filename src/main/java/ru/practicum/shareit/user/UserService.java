package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    Boolean deleteUser(long userId);

    UserDto getUserById(long userId);

    List<UserDto> getAllUsers();

}
