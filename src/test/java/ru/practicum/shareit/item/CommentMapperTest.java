package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CommentMapperTest {
    private Item item;
    private User user;
    private Comment comment;
    private final LocalDateTime start = LocalDateTime.now();

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "John_White", "john@test.com");
        item = new Item(1L, "Item name", "Item Description", true, user,
                null);
        comment = new Comment(1L, "Comment Text", item, user, start);
    }

    @Test
    void commentToDtoTest() {
        CommentDto commentDto = CommentMapper.commentToDto(comment);

        assertThat(commentDto.getId()).isEqualTo(comment.getId());
        assertThat(commentDto.getText()).isEqualTo(comment.getText());
        assertThat(commentDto.getItemId()).isEqualTo(comment.getItem().getId());
        assertThat(commentDto.getAuthorName()).isEqualTo(comment.getAuthor().getName());
        assertThat(commentDto.getCreated()).isEqualTo(comment.getCreated());
    }

}
