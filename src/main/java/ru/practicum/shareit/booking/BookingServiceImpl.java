package ru.practicum.shareit.booking;

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
import java.util.Comparator;
import java.util.List;

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
        return BookingMapper.toBookingDto(bookingRepository.save(tempBooking));
    }

    public BookingDto findBookingById(Long id, Long bookingId) {
        validateUser(id);
        validateBooking(bookingId);

        if (!bookingRepository.getReferenceById(bookingId).getBooker().getId().equals(id) &&
                !bookingRepository.getReferenceById(bookingId).getItem().getOwner().getId().equals(id)) {
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
        return bookingDto;
    }

    public BookingDto confirmOrRejectBooking(Long id, Long bookingId, Boolean approved) {

        if (bookingRepository.getReferenceById(bookingId).getBooker().getId().equals(id)
                && approved && bookingRepository.getReferenceById(bookingId).getId().equals(bookingId)) {
            throw new BookingNotFoundException("Item not found");
        }
        if (approved && bookingRepository.getReferenceById(bookingId).getStatus().equals(BookingStatus.APPROVED)
                && itemRepository.getReferenceById(bookingRepository.getReferenceById(bookingId)
                .getItem().getId()).getOwner().getId().equals(id)) {
            throw new InvalidParameterException("Booking is already approved");
        }
        if (bookingRepository.getReferenceById(bookingId).getId() == null) {
            throw new BookingNotFoundException("Booking not found");
        }

        Booking tempBooking = bookingRepository.getReferenceById(bookingId);

        if (approved) {
            tempBooking.setStatus(BookingStatus.APPROVED);
        } else {
            tempBooking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(tempBooking);
        return BookingMapper.toBookingDto(tempBooking);
    }

    public List<BookingDto> findBookingByIdAndStatus(String state, Long id, Integer from, Integer size) {
        validateUser(id);
        validateState(state);

        if (from != null && size != null) {
            if (from < 0 || size <= 0) {
                throw new InvalidParameterException("Invalid parameter");
            }

            Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
            BookingStatus status = BookingStatus.valueOf(state);
            if (status == BookingStatus.ALL) {

                List<Booking> list = bookingRepository.findBookingsByBookerId(id, pageable).getContent();

                return BookingMapper.toBookingDtos(list);
            }
        }

        BookingStatus status = BookingStatus.valueOf(state);

        if (status.equals(BookingStatus.ALL)) {
            List<Booking> list = bookingRepository.findBookingsByBookerId(id);
            list.sort(Comparator.comparing(Booking::getStart).reversed());
            return BookingMapper.toBookingDtos(list);

        } else if (status.equals(BookingStatus.CURRENT)) {
            List<Booking> list = bookingRepository.findBookingsByBookerIdWithCurrentStatus(id);
            list.sort(Comparator.comparing(Booking::getStart).reversed());
            return BookingMapper.toBookingDtos(list);

        } else if (status.equals(BookingStatus.PAST)) {
            List<Booking> list = bookingRepository.findBookingsByBookerIdWithPastStatus(id);
            list.sort(Comparator.comparing(Booking::getStart).reversed());
            return BookingMapper.toBookingDtos(list);

        } else if (status.equals(BookingStatus.FUTURE)) {
            List<Booking> list = bookingRepository.findBookingsByBookerIdWithFutureStatus(id);
            list.sort(Comparator.comparing(Booking::getStart).reversed());
            return BookingMapper.toBookingDtos(list);

        } else if (status.equals(BookingStatus.WAITING)) {
            List<Booking> list = bookingRepository.findBookingsByBookerIdWithWaitingOrRejectStatus(id,
                    BookingStatus.WAITING);
            list.sort(Comparator.comparing(Booking::getStart).reversed());
            return BookingMapper.toBookingDtos(list);

        } else if (status.equals(BookingStatus.REJECTED)) {
            List<Booking> list = bookingRepository.findBookingsByBookerIdWithWaitingOrRejectStatus(id,
                    BookingStatus.REJECTED);
            list.sort(Comparator.comparing(Booking::getStart).reversed());
            return BookingMapper.toBookingDtos(list);
        }
        return null;
    }

    public List<BookingDto> findAllOwnersBookings(String state, Long id, Integer from, Integer size) {
        validateUser(id);
        validateState(state);

        if (from != null && size != null) {
            if (from < 0 || size <= 0) {
                throw new InvalidParameterException("Invalid parameter");
            }

            Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());

            BookingStatus status = BookingStatus.valueOf(state);
            if (status == BookingStatus.ALL) {

                List<Booking> list = bookingRepository.findAllOwnersBookings(id, pageable).getContent();
                return BookingMapper.toBookingDtos(list);
            }
        }
        BookingStatus status = BookingStatus.valueOf(state);

        if (status == BookingStatus.ALL) {

            List<Booking> list = bookingRepository.findAllOwnersBookings(id);
            list.sort(Comparator.comparing(Booking::getStart).reversed());
            return BookingMapper.toBookingDtos(list);
        }
        if (status == BookingStatus.FUTURE) {
            List<Booking> list = bookingRepository.findAllOwnersBookingsWithFutureStatus(id);
            list.sort(Comparator.comparing(Booking::getStart).reversed());
            return BookingMapper.toBookingDtos(list);
        }
        if (status == BookingStatus.CURRENT) {
            List<Booking> list = bookingRepository.findAllOwnersBookingsWithCurrentStatus(id);
            list.sort(Comparator.comparing(Booking::getStart).reversed());
            return BookingMapper.toBookingDtos(list);
        }
        if (status == BookingStatus.PAST) {
            List<Booking> list = bookingRepository.findAllOwnersBookingsWithPastStatus(id);
            list.sort(Comparator.comparing(Booking::getStart).reversed());
            return BookingMapper.toBookingDtos(list);
        }

        List<Booking> list = bookingRepository.findAllOwnersBookingsWithStatus(id, status);
        list.sort(Comparator.comparing(Booking::getStart).reversed());
        return BookingMapper.toBookingDtos(list);

    }

    private void validateState(String state) {
        if (!state.equals(BookingStatus.ALL.name()) && !state.equals(BookingStatus.REJECTED.name())
                && !state.equals(BookingStatus.WAITING.name()) && !state.equals(BookingStatus.CURRENT.name())
                && !state.equals(BookingStatus.APPROVED.name()) && !state.equals(BookingStatus.CANCELED.name())
                && !state.equals(BookingStatus.PAST.name()) && !state.equals(BookingStatus.FUTURE.name())) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void validateUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
    }

    private void validateBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new BookingNotFoundException("Booking not found");
        }
    }

    private boolean isValidCreate(Long id, BookingSmallDto bookingSmallDto) {

        if (!itemRepository.existsById(bookingSmallDto.getItemId())) {
            throw new ItemNotFoundException("Item not found");
        }
        if (id.equals(itemRepository.getReferenceById(
                bookingSmallDto.getItemId()).getOwner().getId())) {
            throw new ItemNotFoundException("Illegal operation");
        }
        if (bookingSmallDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new InvalidParameterException("End date in past");
        }
        if (bookingSmallDto.getEnd().isBefore(bookingSmallDto.getStart())) {
            throw new InvalidParameterException("End date before start");
        }
        if (bookingSmallDto.getStart().isBefore(LocalDateTime.now())) {
            throw new InvalidParameterException("Start date in past");
        }
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        if (itemRepository.existsById(bookingSmallDto.getItemId())) {
            if (itemRepository.getReferenceById(bookingSmallDto.getItemId()).getIsAvailable() == Boolean.FALSE) {
                throw new InvalidParameterException("Item is unavailable");
            }
        }
        return true;
    }

}
