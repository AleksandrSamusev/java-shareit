package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@Service
public class RequestServiceImpl implements RequestSerivice {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    public RequestServiceImpl(RequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    public RequestDto createRequest(Long id, RequestDto requestDto) {
        validateCreateRequest(id, requestDto);
        Request request = new Request();
          request.setCreated(LocalDateTime.now());
          request.setDescription(requestDto.getDescription());
          request.setRequestor(userRepository.getReferenceById(id));
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    private void validateCreateRequest(Long id, RequestDto requestDto) {
        if (id == null || !userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        if (requestDto.getDescription().isBlank() || requestDto.getDescription().isEmpty()) {
            throw new InvalidParameterException("Description field is empty");
        }
    }
}
