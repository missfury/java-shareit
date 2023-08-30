package ru.practicum.shareit.item.dto;


import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@Builder
public class ItemShortDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
