package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/requests")
public class RequestController {

    public final RequestServiceImpl requestService;

    @Autowired
    public RequestController(RequestServiceImpl requestService) {
        this.requestService = requestService;
    }

    public RequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long id,
                                    RequestDto requestDto) {
        return requestService.createRequest(id, requestDto);
    }
}
