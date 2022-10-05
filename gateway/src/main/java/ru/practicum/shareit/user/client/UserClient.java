package ru.practicum.shareit.user.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.user.entity.UserDto;

@Service
public class UserClient extends UserBaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        return post("", null, userDto);
    }

    public ResponseEntity<Object> updateUser(UserDto userDto, Long userId) {
        return patch("/" + userId, userId, userDto);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("", null);
    }

    public ResponseEntity<Object> getUserById(Long id) {
        return get("/" + id, id);
    }

    public ResponseEntity<Object> deleteUserById(Long id) {
        return delete("/" + id, id);
    }

}