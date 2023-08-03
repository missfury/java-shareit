package ru.practicum.shareit.user;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static User userToModel(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
                );
    }

    public static UserDto userToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
                );
    }

    public static ItemDto.Owner userToOwner(UserDto userDto) {
        return new ItemDto.Owner(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail());
    }
}
