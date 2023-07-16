package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Component
@Repository
public class UserServiceImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long lastUserId;

    @Override
    public User addUser(User user) {
        long userId = ++lastUserId;
        user.setId(userId);
        users.put(userId,user);
        log.info("Пользователь с id: {} добавлен.", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.info("Информация о пользователе с id: {} обновлена.", user.getId());
        return user;
    }

    @Override
    public void deleteUserById(long userId) {
        users.remove(userId);
        log.info("Пользователь с id: {} удален.", userId);
    }

    @Override
    public User findUserById(long userId) {
        return users.get(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean checkUserExist(long userId) {
        return users.containsKey(userId);
    }

    public final Boolean isEmailExisted(String email) {
        for (User userList : users.values()) {
            if (userList.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }
}
