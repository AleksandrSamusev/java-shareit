package ru.practicum.shareit.CommentTest.CommentEntityTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoTest {


    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    public void testCommentDto() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("wrench");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("user");

        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setName("user2");

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("super");
        commentDto.setItem(itemDto);
        commentDto.setAuthor(userDto2);
        commentDto.setAuthorName("user2");
        commentDto.setCreated(LocalDateTime.of(2000, 12, 12, 12, 12));

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("super");
    }

}
