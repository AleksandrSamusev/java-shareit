package ru.practicum.shareit.CommentTest.CommentEntityTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.Comment;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentTest {

    @Autowired
    private JacksonTester<Comment> json;

    @Test
    public void testComment() throws Exception {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("good experience");

        JsonContent<Comment> result = json.write(comment);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("good experience");
    }

}
