package ru.practicum.shareit.request.client;

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
import ru.practicum.shareit.request.entity.RequestDto;

import java.util.Map;

@Service
@Slf4j
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(Long id, RequestDto requestDto) {
        validateCreateRequest(requestDto);
        return post("", id, null, requestDto);
    }

    public ResponseEntity<Object> findAllRequestsWithResponses(Long id) {
        return get("", id,
                null);
    }

    public ResponseEntity<Object> findRequestWithResponses(Long userId, Long requestId) {
        Map<String, Object> parameters = Map.of(
                "requestId", requestId
        );
        return get("/" + requestId, userId, parameters);
    }

    public ResponseEntity<Object> findAllRequestsWithPagination(Long id, Integer from, Integer size) {
        validatePaginationParameters(from, size);
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", id, parameters);
    }


    private void validatePaginationParameters(Integer from, Integer size) {
        if (from != null && from < 0) {
            log.info("InvalidParameterException: Parameter (from) should be > or = 0");
            throw new InvalidParameterException("Parameter \"from\" should be > or = 0");
        }
        if (size != null && size <= 0) {
            log.info("InvalidParameterException: Parameter (size) should be > 0");
            throw new InvalidParameterException("Parameter \"size\" should be > 0");
        }
    }

    private void validateCreateRequest(RequestDto requestDto) {
        if (requestDto.getDescription() == null || requestDto.getDescription().isBlank()
                || requestDto.getDescription().isEmpty()) {
            log.info("InvalidParameterException: Description field is empty");
            throw new InvalidParameterException("Description field is empty");
        }
    }
}
