package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.entity.CommentDto;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.entity.ItemDto;

@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }


    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long id,
                                             @RequestParam(required = false) Long requestId,
                                             @RequestBody ItemDto itemDto) {
        return itemClient.createItem(id, requestId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(id, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader("X-Sharer-User-Id") Long id,
                                               @PathVariable Long itemId) {
        return itemClient.findItemById(id, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllOwnersItems(@RequestHeader("X-Sharer-User-Id") Long id) {
        return itemClient.findAllOwnersItems(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemByString(@RequestParam String text) {
        return itemClient.findItemByString(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader("X-Sharer-User-Id") Long id,
                                              @PathVariable Long itemId,
                                              @RequestBody CommentDto commentDto) {
        return itemClient.postComment(id, itemId, commentDto);
    }
}
