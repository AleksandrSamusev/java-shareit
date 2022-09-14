package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.List;

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
                              @RequestParam Long requestId,
                              @RequestBody ItemDto itemDto) {
        return itemService.createItem(id, requestId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemService.patchItem(itemDto, itemId, id));
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@RequestHeader("X-Sharer-User-Id") Long id,
                                @PathVariable Long itemId) {
        return itemService.findItemById(id, itemId);
    }

    @GetMapping
    public List<ItemDto> findAllOwnersItems(@RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.findAllItemsByOwner(id);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemByString(@RequestParam String text) {
        return itemService.getAllItemsByString(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader("X-Sharer-User-Id") Long id,
                                  @PathVariable Long itemId,
                                  @RequestBody CommentDto commentDto) {
        return itemService.postComment(id, itemId, commentDto);
    }


}
