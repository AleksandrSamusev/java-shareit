package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    Map<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        if (!items.containsValue(item)) {
            item.setId(ItemIdGenerator.generateId());
            items.put(item.getId(), item);
        }
        log.info("добавлена вещь с id = \"{}\"", item.getId());
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        if (items.containsKey(item.getId())) {
            items.put(item.getId(), item);
        }
        log.info("Обновлена вещь с id = \"{}\"", item.getId());
        return item;
    }

    @Override
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        itemList.addAll(items.values());
        return itemList;
    }

    @Override
    public Item findItemById(Long id) {
        for (Item item : getAllItems()) {
            if (item.getId().equals(id)) {
                log.info("Вернулась вещь c id = \"{}\"", item.getId());
                return item;
            }
        }
        return null;
    }

    @Override
    public void deleteItemById(Long id) {
        log.info("Вещь с id = \"{}\" удалена", id);
        items.remove(id);
    }

    public List<Item> findAllItemsByOwner(Long id) {
        List<Item> ownerItems = new ArrayList<>();
        if (id > 0) {
            for (Item itemFromMap : items.values()) {
                if (itemFromMap.getOwner().equals(id)) {
                    ownerItems.add(itemFromMap);
                }
            }
        }
        return ownerItems;
    }
}
