package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotExistException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toModel(userDto);
        if (userRepository.isEmailExisted(user.getEmail())) {
            throw new ValidationException("Пользователь с email = " + userDto.getEmail() + " существует");
        }
        return UserMapper.toDto(userRepository.addUser(user));
    }

    public UserDto updateUser(long userId, UserDto userDto) {
        throwIfUserNotExist(userId);
        User user = userRepository.findUserById(userId);
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            if (userRepository.isEmailExisted(userDto.getEmail())) {
                if (!userDto.getEmail().equals(user.getEmail())) {
                    throw new ValidationException("Пользователь с email " + userDto.getEmail() + " существует");
                }
            }
            user.setEmail(userDto.getEmail());
        }


        return UserMapper.toDto(user);
    }

    public void deleteUser(long userId) {
        throwIfUserNotExist(userId);
        for (Item item : itemRepository.findItemsByUserId(userId)) {
            itemRepository.deleteItemById(item.getId());
        }
        userRepository.deleteUserById(userId);
    }

    public UserDto getUserById(long userId) {
        throwIfUserNotExist(userId);
        return UserMapper.toDto(userRepository.findUserById(userId));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public void throwIfUserNotExist(long userId) {
        if (!userRepository.checkUserExist(userId))
            throw new NotExistException("Пользователь с id " + userId + " не найден");
    }

}
