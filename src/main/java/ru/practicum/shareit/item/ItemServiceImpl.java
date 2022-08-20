package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidParameterException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepositoryImpl itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item createItem(Item item) {
        if (item.getName() == null || item.getName().equals("")) {
            throw new InvalidParameterException("Имя не может быть пустым");
        } else if (item.getDescription() == null || item.getDescription().equals("")) {
            throw new InvalidParameterException("Описание не может быть пустым");
        } else if (item.getIsAvailable() == null) {
            throw new InvalidParameterException("Поле isAvailable не может быть пустым");
        }
        return itemRepository.createItem(item);
    }

    public Item updateItem(Item item) {
        return itemRepository.updateItem(item);
    }

    public List<Item> getAllItems() {
        return itemRepository.getAllItems();
    }

    public Item findItemById(Long id) {
        return itemRepository.findItemById(id);
    }

    public void deleteItemById(Long id) {
        itemRepository.deleteItemById(id);
    }

    @Override
    public List<Item> findAllItemsByOwner(Long id) {
        return itemRepository.findAllItemsByOwner(id);
    }

    @Override
    public List<Item> getAllItemsByString(String someText) {
        List<Item> availableItems = new ArrayList<>();
        if (someText.length() > 0 && !someText.trim().equals("")) {
            for (Item itemFromStorage : itemRepository.getAllItems()) {
                if (itemFromStorage.getIsAvailable()
                        && (itemFromStorage.getDescription().toLowerCase().contains(someText.toLowerCase())
                        || itemFromStorage.getName().toLowerCase().contains(someText.toLowerCase()))) {
                    availableItems.add(itemFromStorage);
                }
            }
        }
        return availableItems;
    }

    @Override
    public Item patchItem(Item item) {
        Item patchedItem = findItemById(item.getId());
        if (item.getName() != null) {
            patchedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            patchedItem.setDescription(item.getDescription());
        }
        if (item.getIsAvailable() != null) {
            patchedItem.setIsAvailable(item.getIsAvailable());
        }
        return patchedItem;
    }
}
