package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RequestServiceImpl implements RequestSerivice {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public RequestServiceImpl(RequestRepository requestRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
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
        if (requestDto.getDescription() == null || requestDto.getDescription().isBlank()
                || requestDto.getDescription().isEmpty()) {
            throw new InvalidParameterException("Description field is empty");
        }
    }

    public List<RequestDtoResponse> findAllRequestsWithResponses(Long id) {
        if (id == null || !userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        List<Request> requestsFromDb = requestRepository.findRequestByRequestorId(id);
        List<RequestDtoResponse> listForResponse = new ArrayList<>();
        for (Request request: requestsFromDb) {
            RequestDtoResponse tempResponse = RequestMapper.toRequestDtoResponse(request);
            tempResponse.setItems(ItemMapper.toItemRequestDtos(itemRepository.findAllByRequestId(request.getId())));
            listForResponse.add(tempResponse);
        }
        return listForResponse;
    }
}
