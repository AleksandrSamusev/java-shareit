package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    List<Item> findAllItemsByOwner(Long id);

    List<Item> getAllItemsByString(String someText);

    Item patchItem(Item item);
}
