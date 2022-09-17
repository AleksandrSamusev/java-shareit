package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {

    public final RequestServiceImpl requestService;

    @Autowired
    public RequestController(RequestServiceImpl requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public RequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long id,
                                    @RequestBody RequestDto requestDto) {
        return requestService.createRequest(id, requestDto);
    }

    @GetMapping
    public List<RequestDtoResponse> findAllRequestsWithResponses (@RequestHeader("X-Sharer-User-Id") Long id) {
        return requestService.findAllRequestsWithResponses(id);
    }

    @GetMapping("/{requestId}")
    public RequestDtoResponse findRequestWithResponses(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @PathVariable Long requestId) {
        return requestService.findRequestWithResponses(userId, requestId);
    }

    @GetMapping("/all")
    public List<RequestDtoResponse> findAllRequestsWithPagination(@RequestHeader("X-Sharer-User-Id") Long id,
                                                                  @RequestParam(required = false) Integer from,
                                                                  @RequestParam(required = false) Integer size) {
        return requestService.findAllRequestsWithPagination(id, from, size);
    }

}
