package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.InvalidParameterException;

@Slf4j
@Service
public class UserClient extends BaseClient {

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

    public ResponseEntity<Object> findAllUsers() {
        log.info("Returned list of Users");
        return get("");
    }

    public ResponseEntity<Object> findUserById(Long id) {
        log.info("returned user with ID = {}", id);
        return get("/", id);
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        validateEmail(userDto);
        log.info("Created user");
        return post("", userDto);
    }

    public ResponseEntity<Object> updateUser(UserDto userDto) {
        log.info("User with ID = {} was updated", userDto.getId());
        return patch("/", userDto);
    }

    public ResponseEntity<Object> deleteUserById(Long id) {
        log.info("User with ID = {} was deleted", id);
        return delete("/", id);
    }

    private void validateEmail(UserDto userDto) {
        if (userDto.getEmail() == null || !userDto.getEmail().contains("@")) {
            log.info("InvalidParameterException: email is null");
            throw new InvalidParameterException("email is null");
        }
    }

}
