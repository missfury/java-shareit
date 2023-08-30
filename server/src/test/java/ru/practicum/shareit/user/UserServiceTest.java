package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private UserDto userDto;
    private User user;

    private User altUser;
    private UserDto altUserDto;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("John_White")
                .email("john@test.com")
                .build();

        userDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();

        altUser = User.builder()
                .id(2L)
                .name("Mary_Sue")
                .email("mary@test.com")
                .build();

        altUserDto = UserDto.builder()
                .id(altUser.getId())
                .name(altUser.getName())
                .email(altUser.getEmail())
                .build();
    }

    @Test
    public void addUserTest() {
        when(userRepository.save(any())).thenReturn(user);
        UserDto newUser = userService.addUser(userDto);

        assertNotNull(newUser);
        assertEquals(user.getId(), newUser.getId());
        assertEquals(user.getEmail(), newUser.getEmail());
        assertThat(user).hasFieldOrProperty("id");
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void addUserWithAlreadyExistsExceptionTest() {
        when(userRepository.save(any())).thenThrow(AlreadyExistsException.class);

        assertThatThrownBy(() -> userService.addUser(userDto)).isInstanceOf(AlreadyExistsException.class);
    }

    @Test
    void addUserWithInvalidEmailTest() {
        userDto.setEmail("invalid_Email");
        when(userRepository.save(any())).thenThrow(ValidationException.class);

        assertThatThrownBy(() -> userService.addUser(userDto)).isInstanceOf(ValidationException.class);
    }

    @Test
    public void getUserTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        UserDto actualUser = userService.getUserById(1L);

        assertNotNull(actualUser);
        assertEquals(user.getId(), actualUser.getId());
        assertEquals(user.getEmail(), actualUser.getEmail());
    }

    @Test
    void getUserWithNotFoundExceptionTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void getAllUsersTest() {
        List<UserDto> expectedUsers = new ArrayList<>();
        expectedUsers.add(userDto);
        expectedUsers.add(altUserDto);

        when(userRepository.findAll()).thenReturn(List.of(user, altUser));

        List<UserDto> actualUsers = userService.getAllUsers();

        assertEquals(expectedUsers.size(), actualUsers.size());

        for (int i = 0; i < expectedUsers.size(); i++) {
            assertEquals(expectedUsers.get(i).getId(), actualUsers.get(i).getId());
            assertEquals(expectedUsers.get(i).getName(), actualUsers.get(i).getName());
            assertEquals(expectedUsers.get(i).getEmail(), actualUsers.get(i).getEmail());
        }
    }

    @Test
    public void updateUserTest() {
        Long userId = altUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(altUser));
        user.setName("New_Name");
        user.setEmail("newemail@test.com");
        UserDto actual = userService.updateUser(userId, UserMapper.userToDto(altUser));

        assertEquals(altUser.getName(), actual.getName());
        assertEquals(altUser.getEmail(), actual.getEmail());
    }

    @Test
    void updateUserWithWrongEmailTest() {
        UserDto altUserDto = new UserDto(999L, "New_Name", "mary@test.com");
        when(userRepository.findById(altUserDto.getId())).thenReturn(Optional.empty());
        Long userId = altUserDto.getId();

        assertThatThrownBy(() -> userService.updateUser(userId, altUserDto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateUserWithAlreadyExistsExceptionTest() {
        UserDto otherUserDto = new UserDto(999L, "John_White", "john@test.com");
        when(userRepository.findById(otherUserDto.getId())).thenThrow(AlreadyExistsException.class);
        Long userId = 999L;
        assertThatThrownBy(() -> userService.updateUser(userId, userDto)).isInstanceOf(AlreadyExistsException.class);
    }

    @Test
    public void deleteUserTest() {
        doNothing().when(userRepository).deleteById(anyLong());
        userService.deleteUser(altUserDto.getId());
        verify(userRepository, times(1)).deleteById(2L);
    }
}
