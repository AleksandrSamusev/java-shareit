package ru.practicum.shareit.booking.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.entity.BookingSmallDto;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, String state, Integer from, Integer size) {
        validatePagination(from, size);
        validateState(state);
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> saveBooking(Long id, BookingSmallDto bookingSmallDto) {
        validateCreate(bookingSmallDto);
        return post("", id, bookingSmallDto);
    }

    public ResponseEntity<Object> findBookingById(Long id, Long bookingId) {
        Map<String, Object> parameters = Map.of(
                "bookingId", bookingId
        );
        return get("/" + bookingId, id, parameters);
    }

    public ResponseEntity<Object> confirmOrRejectBooking(Long id, Long bookingId, Boolean approved) {
        Map<String, Object> parameters = Map.of(
                "bookingId", bookingId,
                "approved", approved
        );
        return patch("/{bookingId}?approved=" + approved, id, parameters, null);
    }

    public ResponseEntity<Object> findAllOwnersBookings(String state, Long id, Integer from, Integer size) {
        validatePagination(from, size);
        validateState(state);
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", id, parameters);
    }

    private void validatePagination(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new InvalidParameterException("Wrong parameter");
        }
    }

    private void validateCreate(BookingSmallDto bookingSmallDto) {

        if (bookingSmallDto.getEnd().isBefore(LocalDateTime.now())) {
            log.info("InvalidParameterException: End date in past");
            throw new InvalidParameterException("End date in past");
        }
        if (bookingSmallDto.getEnd().isBefore(bookingSmallDto.getStart())) {
            log.info("InvalidParameterException: End date before start");
            throw new InvalidParameterException("End date before start");
        }
        if (bookingSmallDto.getStart().isBefore(LocalDateTime.now())) {
            log.info("InvalidParameterException: Start date in past");
            throw new InvalidParameterException("Start date in past");
        }
    }

    private void validateState(String state) {
        if (!state.equals(BookingStatus.ALL.name()) && !state.equals(BookingStatus.REJECTED.name())
                && !state.equals(BookingStatus.WAITING.name()) && !state.equals(BookingStatus.CURRENT.name())
                && !state.equals(BookingStatus.APPROVED.name()) && !state.equals(BookingStatus.CANCELED.name())
                && !state.equals(BookingStatus.PAST.name()) && !state.equals(BookingStatus.FUTURE.name())) {
            log.info("ValidationException: Unknown state: \"{}\"", state);
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

}