package ru.practicum.shareit.comment;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.util.HashSet;

import java.util.Set;

public class CommentMapper {

    public static Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setItem(ItemMapper.toItem(commentDto.getItem()));
        comment.setCreated(commentDto.getCreated());
        comment.setAuthor(UserMapper.toUser(commentDto.getAuthor()));
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setItem(ItemMapper.toItemDto(comment.getItem()));
        commentDto.setCreated(comment.getCreated());
        commentDto.setAuthor(UserMapper.toUserDto(comment.getAuthor()));
        return commentDto;
    }

    public static Set<CommentDto> toCommentDtos(Set<Comment> comments) {
        Set<CommentDto> dtos = new HashSet<>();
        for (Comment comment : comments) {
            dtos.add(toCommentDto(comment));
        }
        return dtos;
    }

    public static Set<Comment> toComment(Set<CommentDto> commentsDto) {
        Set<Comment> dtos = new HashSet<>();
        for (CommentDto commentDto : commentsDto) {
            dtos.add(toComment(commentDto));
        }
        return dtos;
    }
}
