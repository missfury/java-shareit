package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info(String.format("POST /users, body = %s", userDto));
        final UserDto user = userService.addUser(userDto);
        log.info(String.format("Успешно добавлен пользователь с id = %s", user.getId()));
        return user;
    }

    @ResponseBody
    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.info(String.format("PATCH /users/{userId}, body = %s, {userId} = %s", userDto, userId));
        final UserDto user = userService.updateUser(userId, userDto);
        log.info(String.format("Успешно обновлена информация о пользователе с id = %s", user.getId()));
        return user;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info(String.format("DELETE /users/{userId}, {userId} = %s", userId));
        userService.deleteUser(userId);
        log.info(String.format("Пользователь с id = %s успешно удален", userId));
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("GET /users");
        final List<UserDto> users = userService.getAllUsers();
        log.info(String.format("Успешно получены пользователи (%s шт.)", users.size()));
        return users;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info(String.format("GET /users/{userId}, {userId} = %s", userId));
        final UserDto user = userService.getUserById(userId);
        log.info(String.format("Получена информация о пользователе с id = %s", user.getId()));
        return user;
    }
}
