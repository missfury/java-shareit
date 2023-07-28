package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotEmpty(message = "Поле name обязательно для заполнения")
    private String name;
    @NotEmpty(message = "Поле description обязательно для заполнения")
    private String description;
    @NotNull
    private Boolean available;
}
