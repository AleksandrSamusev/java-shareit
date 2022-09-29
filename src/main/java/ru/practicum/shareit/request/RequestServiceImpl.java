package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public RequestServiceImpl(RequestRepository requestRepository, UserRepository userRepository,
                              ItemRepository itemRepository) {
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
        log.info("User with ID = {} create new request", id);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    public List<RequestDtoResponse> findAllRequestsWithResponses(Long id) {
        validateUserId(id);
        List<Request> requestsFromDb = requestRepository.findRequestByRequestorId(id);
        List<RequestDtoResponse> listForResponse = new ArrayList<>();
        for (Request request : requestsFromDb) {
            RequestDtoResponse tempResponse = RequestMapper.toRequestDtoResponse(request);
            tempResponse.setItems(ItemMapper.toItemRequestDtos(itemRepository.findAllByRequestId(request.getId())));
            listForResponse.add(tempResponse);
        }
        log.info("Returned list with responses. List size = {}", listForResponse.size());
        return listForResponse;
    }

    public RequestDtoResponse findRequestWithResponses(Long userId, Long requestId) {
        validateUserId(userId);
        validateRequestId(requestId);
        RequestDtoResponse response = RequestMapper.toRequestDtoResponse(requestRepository.findRequestById(requestId));
        response.setItems(ItemMapper.toItemRequestDtos(itemRepository.findAllByRequestId(requestId)));
        log.info("Returned request with ID = {}", requestId);
        return response;

    }

    public List<RequestDtoResponse> findAllRequestsWithPagination(Long userId, Integer from, Integer size) {
        validateUserId(userId);
        validatePaginationParameters(from, size);
        if (from != null && size != null) {
            Pageable pageable = PageRequest.of(from, size, Sort.by("created").descending());
            List<Request> requestsFromDb = requestRepository.findOthersRequestsWithPagination(userId, pageable);
            List<RequestDtoResponse> listForResponse = new ArrayList<>();
            for (Request request : requestsFromDb) {
                RequestDtoResponse tempResponse = RequestMapper.toRequestDtoResponse(request);
                tempResponse.setItems(ItemMapper.toItemRequestDtos(itemRepository.findAllByRequestId(request.getId())));
                listForResponse.add(tempResponse);
            }
            log.info("Returned {} requests from page {}", size, from / size);
            return listForResponse;
        }
        log.info("Returned list of all requests with responses");
        return findAllRequestsWithResponses(userId);
    }

    private void validateCreateRequest(Long id, RequestDto requestDto) {
        validateUserId(id);
        if (requestDto.getDescription() == null || requestDto.getDescription().isBlank()
                || requestDto.getDescription().isEmpty()) {
            log.info("InvalidParameterException: Description field is empty");
            throw new InvalidParameterException("Description field is empty");
        }
    }

    private void validateUserId(Long id) {
        if (id == null || !userRepository.existsById(id)) {
            log.info("UserNotFoundException: User with ID = {} not found", id);
            throw new UserNotFoundException("User not found");
        }
    }

    private void validateRequestId(Long id) {
        if (!requestRepository.existsById(id)) {
            log.info("RequestNotFoundException: Request with ID = {} not found", id);
            throw new RequestNotFoundException("Request not found");
        }
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
}
