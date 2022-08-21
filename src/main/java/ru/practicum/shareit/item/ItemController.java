package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;

    @Autowired
    public ItemController(ItemServiceImpl itemService, UserServiceImpl userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long id,
                              @RequestBody ItemDto itemDto) {
       return itemService.createDtoItem(itemDto, id);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable Long itemId,
                           @RequestBody Item item) {
        return itemService.updateItem(itemService.patchItem(item, itemId, id));
    }

    @GetMapping("/{itemId}")
    public Item findItemById(@PathVariable Long itemId) {
        return itemService.findItemById(itemId);
    }

    @GetMapping
    public List<Item> findAllOwnersItems(@RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.findAllItemsByOwner(id);
    }

    @GetMapping("/search")
    public List<Item> findItemByString(@RequestParam String text) {
        return itemService.getAllItemsByString(text);
    }

}
