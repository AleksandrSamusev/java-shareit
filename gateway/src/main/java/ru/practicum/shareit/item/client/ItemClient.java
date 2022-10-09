package ru.practicum.shareit.item.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comment.entity.CommentDto;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.item.entity.ItemDto;

import java.util.Map;

@Service
@Slf4j
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(Long id, ItemDto itemDto) {
        validateItemDto(itemDto);

        return post("", id, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long itemId, Long id, ItemDto itemDto) {
        return patch("/" + itemId, id, itemDto);
    }

    public ResponseEntity<Object> findItemById(Long id, Long itemId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return get("/" + itemId, id, parameters);
    }

    public ResponseEntity<Object> findAllOwnersItems(Long id) {
        return get("", id, null);
    }

    public ResponseEntity<Object> findItemByString(String text) {
        return get("/search?text=" + text,
                null, null);
    }

    public ResponseEntity<Object> postComment(Long id, Long itemId, CommentDto commentDto) {
        validateComment(commentDto);
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return post("/" + itemId + "/comment", id, parameters, commentDto);
    }

    private void validateItemDto(ItemDto itemDto) {
        if (itemDto.getIsAvailable() == null) {
            throw new InvalidParameterException("Item isAvailable is empty");
        } else if (itemDto.getName() == null || itemDto.getName().equals("")) {
            throw new InvalidParameterException("Item name is empty");
        } else if (itemDto.getDescription() == null || itemDto.getDescription().equals("")) {
            throw new InvalidParameterException("Item description is empty");
        }
    }

    private void validateComment(CommentDto commentDto) {
        if (commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            log.info("InvalidParameterException: Text field is empty");
            throw new InvalidParameterException("Text field is empty");
        }
    }
}
