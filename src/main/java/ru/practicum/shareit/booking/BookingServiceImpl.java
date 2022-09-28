package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public BookingDto create(Long id, BookingSmallDto bookingSmallDto) {
        Booking tempBooking = new Booking();
        if (isValidCreate(id, bookingSmallDto)) {
            tempBooking.setItem(itemRepository.getReferenceById(bookingSmallDto.getItemId()));
            tempBooking.setStart(bookingSmallDto.getStart());
            tempBooking.setEnd(bookingSmallDto.getEnd());
            tempBooking.setBooker(userRepository.getReferenceById(id));
        }
        log.info("Create booking by user with id = {}", id);
        return BookingMapper.toBookingDto(bookingRepository.save(tempBooking));
    }

    public BookingDto findBookingById(Long id, Long bookingId) {
        validateUser(id);
        validateBooking(bookingId);
        if (!bookingRepository.getReferenceById(bookingId).getBooker().getId().equals(id) &&
                !bookingRepository.getReferenceById(bookingId).getItem().getOwner().getId().equals(id)) {
            log.info("Request can't be completed. Not booker or not user");
            throw new BookingNotFoundException("Booking not found");
        }
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setStart(bookingRepository.getReferenceById(bookingId).getStart());
        bookingDto.setEnd(bookingRepository.getReferenceById(bookingId).getEnd());
        bookingDto.setStatus(bookingRepository.getReferenceById(bookingId).getStatus());
        bookingDto.setBooker(new UserDto(bookingRepository.getReferenceById(bookingId).getBooker().getId()));
        bookingDto.setItem(new ItemDto(bookingRepository.getReferenceById(bookingId).getItem().getId(),
                itemRepository.getReferenceById(bookingRepository.getReferenceById(bookingId)
                        .getItem().getId()).getName()));
        log.info("Return booking with id = {}", bookingId);
        return bookingDto;
    }

    public BookingDto confirmOrRejectBooking(Long id, Long bookingId, Boolean approved) {

        if (bookingRepository.getReferenceById(bookingId).getBooker().getId().equals(id)
                && approved && bookingRepository.getReferenceById(bookingId).getId().equals(bookingId)) {
            log.info("Incorrect request");
            throw new BookingNotFoundException("Item not found");
        }
        if (approved && bookingRepository.getReferenceById(bookingId).getStatus().equals(BookingStatus.APPROVED)
                && itemRepository.getReferenceById(bookingRepository.getReferenceById(bookingId)
                .getItem().getId()).getOwner().getId().equals(id)) {
            log.info("Booking is already approved");
            throw new InvalidParameterException("Booking is already approved");
        }
        if (bookingRepository.getReferenceById(bookingId).getId() == null) {
            log.info("Booking not found");
            throw new BookingNotFoundException("Booking not found");
        }

        Booking tempBooking = bookingRepository.getReferenceById(bookingId);

        if (approved) {
            tempBooking.setStatus(BookingStatus.APPROVED);
        } else {
            tempBooking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(tempBooking);
        log.info("Booking status set to {}", tempBooking.getStatus());
        return BookingMapper.toBookingDto(tempBooking);
    }

    public List<BookingDto> findBookingByIdAndStatus(String state, Long id, Integer from, Integer size) {
        validateUser(id);
        validateState(state);
        validatePagination(from, size);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        BookingStatus status = BookingStatus.valueOf(state);
        List<Booking> list = new ArrayList<>();

        if (status == BookingStatus.ALL) {
            list = bookingRepository.findBookingsByBookerId(id, pageable);

        } else if (status == BookingStatus.CURRENT) {
            list = bookingRepository.findBookingsByBookerIdWithCurrentStatus(id, pageable);

        } else if (status == BookingStatus.PAST) {
            list = bookingRepository.findBookingsByBookerIdWithPastStatus(id, pageable);

        } else if (status == BookingStatus.FUTURE) {
            list = bookingRepository.findBookingsByBookerIdWithFutureStatus(id, pageable);

        } else if (status == BookingStatus.REJECTED || status == BookingStatus.WAITING) {
            list = bookingRepository.findBookingsByBookerIdWithWaitingOrRejectStatus(id, status, pageable);
        }
        log.info("Returned BockingDto list with size = {}", list.size());
        return BookingMapper.toBookingDtos(list);
    }

    public List<BookingDto> findAllOwnersBookings(String state, Long id, Integer from, Integer size) {
        validateUser(id);
        validateState(state);
        validatePagination(from, size);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        List<Booking> list = new ArrayList<>();
        BookingStatus status = BookingStatus.valueOf(state);

        if (status == BookingStatus.ALL) {
            list = bookingRepository.findAllOwnersBookings(id, pageable);

        } else if (status == BookingStatus.FUTURE) {
            list = bookingRepository.findAllOwnersBookingsWithFutureStatus(id, pageable);

        } else if (status == BookingStatus.CURRENT) {
            list = bookingRepository.findAllOwnersBookingsWithCurrentStatus(id, pageable);

        } else if (status == BookingStatus.PAST) {
            list = bookingRepository.findAllOwnersBookingsWithPastStatus(id, pageable);

        } else if (status == BookingStatus.WAITING || status == BookingStatus.REJECTED) {
            list = bookingRepository.findAllOwnersBookingsWithStatus(id, status, pageable);
        }
        log.info("Returned BockingDto list with size = {}", list.size());
        return BookingMapper.toBookingDtos(list);
    }

    private void validateState(String state) {
        if (!state.equals(BookingStatus.ALL.name()) && !state.equals(BookingStatus.REJECTED.name())
                && !state.equals(BookingStatus.WAITING.name()) && !state.equals(BookingStatus.CURRENT.name())
                && !state.equals(BookingStatus.APPROVED.name()) && !state.equals(BookingStatus.CANCELED.name())
                && !state.equals(BookingStatus.PAST.name()) && !state.equals(BookingStatus.FUTURE.name())) {
            log.info("ValidationException: Unknown state: UNSUPPORTED_STATUS");
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void validateUser(Long id) {
        if (!userRepository.existsById(id)) {
            log.info("UserNotFoundException: User not found");
            throw new UserNotFoundException("User not found");
        }
    }

    private void validateBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            log.info("BookingNotFoundException: Booking not found");
            throw new BookingNotFoundException("Booking not found");
        }
    }


    private boolean isValidCreate(Long id, BookingSmallDto bookingSmallDto) {
        if (!itemRepository.existsById(bookingSmallDto.getItemId())) {
            log.info("ItemNotFoundException: Item not found");
            throw new ItemNotFoundException("Item not found");
        }
        if (id.equals(itemRepository.getReferenceById(
                bookingSmallDto.getItemId()).getOwner().getId())) {
            log.info("ItemNotFoundException: Illegal operation");
            throw new ItemNotFoundException("Illegal operation");
        }
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
        if (!userRepository.existsById(id)) {
            log.info("UserNotFoundException: User not found");
            throw new UserNotFoundException("User not found");
        }
        if (itemRepository.existsById(bookingSmallDto.getItemId())) {
            if (itemRepository.getReferenceById(bookingSmallDto.getItemId()).getIsAvailable() == Boolean.FALSE) {
                log.info("InvalidParameterException: Item is unavailable");
                throw new InvalidParameterException("Item is unavailable");
            }
        }
        return true;
    }

    private void validatePagination(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            log.info("InvalidParameterException: Wrong parameter");
            throw new InvalidParameterException("Wrong parameter");
        }
    }

}
