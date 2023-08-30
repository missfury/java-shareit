package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank
    @Size(max = 500)
    private String name;

    @NotBlank
    @Size(max = 1000)
    private String description;

    @NotNull
    private Boolean available;

    private Long requestId;
    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments;
}

