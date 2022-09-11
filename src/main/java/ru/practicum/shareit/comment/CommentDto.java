package ru.practicum.shareit.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDto {

    private Long id;
    private String text;
    private ItemDto item;
    private UserDto author;
    private String authorName;
    private LocalDateTime created;
}
