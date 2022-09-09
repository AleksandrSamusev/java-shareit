package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    List<ItemDto> findAllItemsByOwner(Long id);

    List<ItemDto> getAllItemsByString(String someText);

    ItemDto patchItem(ItemDto itemDto, Long itemId, Long id);

}
