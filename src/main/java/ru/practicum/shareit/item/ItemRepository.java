package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {

    public Item createItem(Item item);

    public Item updateItem(Item item);

    public List<Item> getAllItems();

    public Item findItemById(Long id);

    public void deleteItemById(Long id);

    public List<Item> findAllItemsByOwner(Long id);
}
