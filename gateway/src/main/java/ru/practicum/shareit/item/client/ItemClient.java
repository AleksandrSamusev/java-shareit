package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.comment.entity.CommentDto;
import ru.practicum.shareit.item.entity.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends ItemBaseClient {

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

    public ResponseEntity<Object> createItem(Long id, Long requestId, ItemDto itemDto) {
        Map<String, Object> parameters = Map.of(
                "requestId", requestId
        );
        return post("", id, parameters, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long id, Long itemId, ItemDto itemDto) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return patch("/" + itemId, id, parameters, itemDto);
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
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text=" + text, null, null);
    }

    public ResponseEntity<Object> postComment(Long id, Long itemId, CommentDto commentDto) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return post("/" + itemId + "/comment", id, parameters, commentDto);
    }
}
