package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserRequestDto;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody UserRequestDto userRequestDto) {
        log.info("Create user {}", userRequestDto);
        return userClient.addUser(userRequestDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("Get user with id = {}", id);
        return userClient.getUserById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Get all users");
        return userClient.getUsers();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUserById(@RequestBody UpdateUserDto updateUserDto,
                                                 @PathVariable Long id) {
        log.info("Update user with id = {}", id);
        return userClient.updateUserById(id, updateUserDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> removeUserById(@PathVariable Long id) {
        log.info("Remove user with id = {}", id);
        return userClient.removeUserById(id);
    }
}
