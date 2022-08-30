package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {

   Item createItem(Item item);

   Item updateItem(Item item);

    List<Item> getAllItems();

    Item findItemById(Long id);

    void deleteItemById(Long id);

    List<Item> findAllItemsByOwner(Long id);
}
