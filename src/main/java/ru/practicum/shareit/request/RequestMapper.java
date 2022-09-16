package ru.practicum.shareit.request;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.UserMapper;

import java.util.ArrayList;
import java.util.List;

@Component
public class RequestMapper {

    public static Request toRequest(RequestDto requestDto) {
        Request request = new Request();
        request.setId(requestDto.getId());
        request.setDescription(requestDto.getDescription());
        request.setRequestor(UserMapper.toUser(requestDto.getRequestor()));
        request.setCreated(requestDto.getCreated());
        return request;
    }

    public static RequestDto toRequestDto(Request request) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setRequestor(UserMapper.toUserDto(request.getRequestor()));
        requestDto.setCreated(request.getCreated());
        return requestDto;
    }

    public static List<RequestDto> toRequestDtos(List<Request> requests) {
        List<RequestDto> requestDtos = new ArrayList<>();
        for (Request request: requests) {
            requestDtos.add(toRequestDto(request));
        }
        return requestDtos;
    }

    public static List<Request> toRequests(List<RequestDto> requestDtos) {
        List<Request> requests = new ArrayList<>();
        for (RequestDto requestDto: requestDtos) {
            requests.add(toRequest(requestDto));
        }
        return requests;
    }

    public static RequestDtoResponse toRequestDtoResponse(Request request) {
        RequestDtoResponse requestDtoResponse = new RequestDtoResponse();
        requestDtoResponse.setId(request.getId());
        requestDtoResponse.setCreated(request.getCreated());
        requestDtoResponse.setDescription(request.getDescription());
        return requestDtoResponse;
    }

    public static List<RequestDtoResponse> toRequestResponseDtos(List<Request> requests) {
        List<RequestDtoResponse> temp = new ArrayList<>();
        for (Request request: requests) {
            temp.add(toRequestDtoResponse(request));
        }
        return temp;
    }
}
