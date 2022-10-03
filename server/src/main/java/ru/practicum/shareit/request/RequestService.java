package ru.practicum.shareit.request;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(Long id, RequestDto requestDto);

    List<RequestDtoResponse> findAllRequestsWithResponses(Long id);

    RequestDtoResponse findRequestWithResponses(Long userId, Long requestId);

    List<RequestDtoResponse> findAllRequestsWithPagination(Long userId, Integer from, Integer size);


}
