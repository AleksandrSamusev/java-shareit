package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.exception.RequestNotFoundException;
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

    public List<RequestDtoResponse> findRequestWithResponses(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
        if (!requestRepository.existsById(requestId)) {
            throw new RequestNotFoundException("Request not found");
        }
        List<Request> requestsFromDb = requestRepository.findRequestById(requestId);
        List<RequestDtoResponse> listForResponse = new ArrayList<>();
        for (Request request: requestsFromDb) {
            RequestDtoResponse tempResponse = RequestMapper.toRequestDtoResponse(request);
            tempResponse.setItems(ItemMapper.toItemRequestDtos(itemRepository.findAllByRequestId(requestId)));
            listForResponse.add(tempResponse);
        }
        return listForResponse;
    }

    public List<RequestDtoResponse> findAllRequestsWithPagination(Long userId, Integer from, Integer size) {
        if (userId == null || !userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
        if (from != null) {
            if (from < 0) {
                throw new InvalidParameterException("Parameter \"from\" should be > or = 0");
            }
        }
        if (size != null) {
            if (size <= 0) {
                throw new InvalidParameterException("Parameter \"size\" should be > 0");
            }
        }
        if (from != null && size != null) {
            Pageable pageable = PageRequest.of(from, size, Sort.by("created").descending());
            List<Request> requestsFromDb = requestRepository.findOthersRequestsWithPagination(userId, pageable);
            List<RequestDtoResponse> listForResponse = new ArrayList<>();
            for (Request request : requestsFromDb) {
                RequestDtoResponse tempResponse = RequestMapper.toRequestDtoResponse(request);
                tempResponse.setItems(ItemMapper.toItemRequestDtos(itemRepository.findAllByRequestId(request.getId())));
                listForResponse.add(tempResponse);
            }
            return listForResponse;
        }
        return findAllRequestsWithResponses(userId);
    }
}
