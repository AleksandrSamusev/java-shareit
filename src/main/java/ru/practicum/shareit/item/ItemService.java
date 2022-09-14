package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.CommentDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> findAllItemsByOwner(Long id);

    List<ItemDto> getAllItemsByString(String someText);

    ItemDto patchItem(ItemDto itemDto, Long itemId, Long id);

    ItemDto findItemById(Long userId, Long itemId);

    CommentDto postComment(Long userId, Long itemId, CommentDto commentDto);
}
