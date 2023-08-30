package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;

    private String text;

    private Long itemId;

    private String authorName;

    private LocalDateTime created;
}