package ru.practicum.shareit.request;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.entity.RequestDto;

@RestController
@RequestMapping("/requests")
public class RequestController {

    public final RequestClient requestClient;

    public RequestController(RequestClient requestClient) {
        this.requestClient = requestClient;
    }


    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long id,
                                                @RequestBody RequestDto requestDto) {
        return requestClient.createRequest(id, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllRequestsWithResponses(@RequestHeader("X-Sharer-User-Id") Long id) {
        return requestClient.findAllRequestsWithResponses(id);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequestWithResponses(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @PathVariable Long requestId) {
        return requestClient.findRequestWithResponses(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequestsWithPagination(@RequestHeader("X-Sharer-User-Id") Long id,
                                                                @RequestParam(required = false,
                                                                        defaultValue = "0") Integer from,
                                                                @RequestParam(required = false,
                                                                        defaultValue = "10") Integer size) {
        return requestClient.findAllRequestsWithPagination(id, from, size);
    }

}
