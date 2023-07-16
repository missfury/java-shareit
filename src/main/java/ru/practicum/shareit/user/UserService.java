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
        throwIfEmailExist(user.getEmail());
        return UserMapper.toDto(userRepository.addUser(user));
    }

    public UserDto updateUser(long userId, UserDto userDto) {
        throwIfUserNotExist(userId);
        return UserMapper.toDto(userRepository.updateUser(updateUserInfo(userId, userDto)));
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
        return UserMapper.toDto(userRepository.findItemById(userId));
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

    private void throwIfEmailExist(String email) {
        String newUserEmail = email.toLowerCase();
        for (User existingUser : userRepository.findAll()) {
            if (newUserEmail.equals(existingUser.getEmail().toLowerCase())) {
                throw new ValidationException("Пользователь с email " + newUserEmail + " уже существует");
            }
        }
    }

    private User updateUserInfo(long userId, UserDto userDto) {
        User userInfo = UserMapper.toModel(userDto);
        User oldUserInfo = userRepository.findItemById(userId);
        if (userInfo.getEmail() == null) {
            userInfo.setEmail(oldUserInfo.getEmail());
        } else {
            throwIfEmailExist(userInfo.getEmail());
        }
        if (userInfo.getName() == null)
            userInfo.setName(oldUserInfo.getName());
        userInfo.setId(userId);
        return userInfo;
    }
}
