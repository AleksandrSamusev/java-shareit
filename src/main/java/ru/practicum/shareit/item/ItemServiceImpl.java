package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserServiceImpl userService;

    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    public Item updateItem(Item item) {
        Item temp = itemRepository.getReferenceById(item.getId());
        if (item.getName()!=null && !item.getName().equals("")) {
            temp.setName(item.getName());
        }
        if(item.getIsAvailable() != null) {
            temp.setIsAvailable(item.getIsAvailable());
        }
        if(item.getDescription()!=null && !item.getDescription().equals("")) {
            temp.setDescription(item.getDescription());
        }
        if(item.getOwnerId()!=null && item.getOwnerId()!=0) {
            temp.setOwnerId(item.getOwnerId());
        }
        if(item.getRequestId()!=null && item.getRequestId()!=0) {
            temp.setRequestId(item.getRequestId());
        }
        return itemRepository.save(temp);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item findItemById(Long id) {
        return itemRepository.getReferenceById(id);
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
        if (!Objects.equals(itemRepository.getReferenceById(itemId).getOwnerId(), id)) {
            throw new ItemNotFoundException("Вещь не принадлежит юзеру");
        }
        item.setId(itemId);

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

    @Override
    public ItemDto createDtoItem(ItemDto itemDto, Long id) {
        if (userService.findUserById(id) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        itemDto.setOwnerId(id);
        Item item = createItem(ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }
}
