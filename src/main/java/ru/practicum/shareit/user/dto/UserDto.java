package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class UserDto {
    private long id;
    @NotEmpty(message = "Поле name обязательно для заполнения")
    private String name;
    @Email(message = "Значение в поле email не соответствует формату")
    @NotEmpty(message = "Поле email обязательно для заполнения")
    private String email;
}
