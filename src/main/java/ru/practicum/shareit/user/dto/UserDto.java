package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    @NotEmpty(message = "Поле name обязательно для заполнения")
    private String name;
    @Email(message = "Значение в поле email не соответствует формату")
    @NotEmpty(message = "Поле email обязательно для заполнения")
    private String email;
}
