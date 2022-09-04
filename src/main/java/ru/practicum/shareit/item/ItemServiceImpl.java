package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service

public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserServiceImpl userService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserServiceImpl userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    public Item createItem(Item item) {
        if (item.getIsAvailable() == null) {
            throw new InvalidParameterException("Item isAvailable is empty");
        } else if (item.getName() == null || item.getName().equals("")) {
            throw new InvalidParameterException("Item name is empty");
        } else if (item.getDescription() == null || item.getDescription().equals("")) {
            throw new InvalidParameterException("Item description is empty");
        }
        return itemRepository.save(item);
    }

    public Item updateItem(Item item) {
        Item temp = itemRepository.getReferenceById(item.getId());
        if (item.getName() != null && !item.getName().equals("")) {
            temp.setName(item.getName());
        }
        if (item.getIsAvailable() != null) {
            temp.setIsAvailable(item.getIsAvailable());
        }
        if (item.getDescription() != null && !item.getDescription().equals("")) {
            temp.setDescription(item.getDescription());
        }
        if (item.getOwnerId() != null && item.getOwnerId() != 0) {
            temp.setOwnerId(item.getOwnerId());
        }
        if (item.getRequestId() != null && item.getRequestId() != 0) {
            temp.setRequestId(item.getRequestId());
        }
        return itemRepository.save(temp);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> findItemById(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException("Item not found");
        }
        return itemRepository.findById(id);
    }

    public void deleteItemById(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<Item> findAllItemsByOwner(Long id) {
        return itemRepository.findAllItemsByOwner(id);
    }

    @Override
    public List<Item> getAllItemsByString(String someText) {
        List<Item> availableItems = new ArrayList<>();
        if (someText.length() > 0 && !someText.trim().equals("")) {
            for (Item itemFromStorage : itemRepository.findAll()) {
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
    public Item patchItem(Item item, Long itemId, Long id) {
        if (findItemById(itemId).isPresent()) {
            if (!Objects.equals(findItemById(itemId).get().getOwnerId(), id)) {
                throw new ItemNotFoundException("Вещь не принадлежит юзеру");
            }
        }
        item.setId(itemId);
        if (findItemById(item.getId()).isPresent()) {
            Item patchedItem = findItemById(item.getId()).get();
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
        } else {
            throw new ItemNotFoundException("Item not found");
        }
    }

    @Override
    public ItemDto createDtoItem(ItemDto itemDto, Long id) {
        if (userService.findUserById(id).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        itemDto.setOwnerId(id);
        Item item = createItem(ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }
}
