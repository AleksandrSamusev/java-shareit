package ru.practicum.shareit.request;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.UserMapper;

@Component
public class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setRequestor(UserMapper.toUserDto(request.getRequestor()));
        requestDto.setCreated(request.getCreated());
        return requestDto;
    }

    public static RequestDtoResponse toRequestDtoResponse(Request request) {
        RequestDtoResponse requestDtoResponse = new RequestDtoResponse();
        requestDtoResponse.setId(request.getId());
        requestDtoResponse.setCreated(request.getCreated());
        requestDtoResponse.setDescription(request.getDescription());
        return requestDtoResponse;
    }

}
