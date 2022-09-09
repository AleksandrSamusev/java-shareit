package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service

public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserServiceImpl userService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserServiceImpl userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    public ItemDto createItem(Long id, ItemDto itemDto) {
        if (itemDto.getIsAvailable() == null) {
            throw new InvalidParameterException("Item isAvailable is empty");
        } else if (itemDto.getName() == null || itemDto.getName().equals("")) {
            throw new InvalidParameterException("Item name is empty");
        } else if (itemDto.getDescription() == null || itemDto.getDescription().equals("")) {
            throw new InvalidParameterException("Item description is empty");
        }
        itemDto.setOwner(userService.findUserById(id));
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto)));
    }

    public ItemDto updateItem(ItemDto itemDto) {
        Item temp = itemRepository.getReferenceById(itemDto.getId());
        if (itemDto.getName() != null && !itemDto.getName().equals("")) {
            temp.setName(itemDto.getName());
        }
        if (itemDto.getIsAvailable() != null) {
            temp.setIsAvailable(itemDto.getIsAvailable());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().equals("")) {
            temp.setDescription(itemDto.getDescription());
        }
        if (itemDto.getOwner().getId() != null && itemDto.getOwner().getId() != 0) {
            temp.setOwner(UserMapper.toUser(itemDto.getOwner()));
        }
        if (itemDto.getRequestId() != null && itemDto.getRequestId() != 0) {
            temp.setRequestId(itemDto.getRequestId());
        }
        return ItemMapper.toItemDto(itemRepository.save(temp));
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public ItemDto findItemById(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException("Item not found");
        }
        return ItemMapper.toItemDto(itemRepository.getReferenceById(id));
    }

    public void deleteItemById(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> findAllItemsByOwner(Long id) {
        return ItemMapper.toItemDtos(itemRepository.findAllItemsByOwner(id));
    }

    @Override
    public List<ItemDto> getAllItemsByString(String someText) {
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
        return ItemMapper.toItemDtos(availableItems);
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, Long itemId, Long id) {
        if (findItemById(itemId) != null) {
            if (!Objects.equals(findItemById(itemId).getOwner().getId(), id)) {
                throw new ItemNotFoundException("Вещь не принадлежит юзеру");
            }
        }
        itemDto.setId(itemId);
        if (findItemById(itemDto.getId()) != null) {
            ItemDto patchedItem = findItemById(itemDto.getId());
            if (itemDto.getName() != null) {
                patchedItem.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                patchedItem.setDescription(itemDto.getDescription());
            }
            if (itemDto.getIsAvailable() != null) {
                patchedItem.setIsAvailable(itemDto.getIsAvailable());
            }
            return patchedItem;
        } else {
            throw new ItemNotFoundException("Item not found");
        }
    }
}
