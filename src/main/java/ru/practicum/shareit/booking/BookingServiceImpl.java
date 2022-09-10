package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
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
public class BookingServiceImpl {

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
        if (bookingSmallDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new InvalidParameterException("End date in past");
        }
        if (bookingSmallDto.getEnd().isBefore(bookingSmallDto.getStart())) {
            throw new InvalidParameterException("End date before start");
        }
        if (bookingSmallDto.getStart().isBefore(LocalDateTime.now())) {
            throw new InvalidParameterException("Start date in past");
        }
        if (!itemRepository.existsById(bookingSmallDto.getItemId())) {
            throw new ItemNotFoundException("Item not found");
        }
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        if (itemRepository.existsById(bookingSmallDto.getItemId())) {
            if (itemRepository.getReferenceById(bookingSmallDto.getItemId()).getIsAvailable() == Boolean.FALSE) {
                throw new InvalidParameterException("Item is unavailable");
            }
        }

        Booking tempBooking = new Booking();
        tempBooking.setItem(itemRepository.getReferenceById(bookingSmallDto.getItemId()));
        tempBooking.setStart(bookingSmallDto.getStart());
        tempBooking.setEnd(bookingSmallDto.getEnd());
        tempBooking.setBooker(userRepository.getReferenceById(id));

        return BookingMapper.toBookingDto(bookingRepository.save(tempBooking));
    }

    public BookingDto findBookingById(Long id, Long bookingId) {
        if (userRepository.existsById(id)) {
            if (bookingRepository.existsById(bookingId)) {
                if (!bookingRepository.getReferenceById(bookingId).getBooker().getId().equals(id) &&
                        !bookingRepository.getReferenceById(bookingId).getItem().getOwner().getId().equals(id)) {
                    throw new BookingNotFoundException("Booking not found");
                }
                BookingDto bookingDto = new BookingDto();
                bookingDto.setId(bookingId);
                bookingDto.setStart(bookingRepository.getReferenceById(bookingId).getStart());
                bookingDto.setEnd(bookingRepository.getReferenceById(bookingId).getEnd());
                bookingDto.setStatus(bookingRepository.getReferenceById(bookingId).getStatus());
                bookingDto.setBooker(new UserDto(bookingRepository.getReferenceById(bookingId)
                        .getBooker().getId()));
                bookingDto.setItem(new ItemDto(bookingRepository.getReferenceById(bookingId).getItem().getId(),
                        itemRepository.getReferenceById(bookingRepository.getReferenceById(bookingId)
                                .getItem().getId()).getName()));
                return bookingDto;
            } else {
                throw new BookingNotFoundException("Booking not found");
            }
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    public BookingDto confirmOrRejectBooking(Long id, Long bookingId, Boolean approved) {
        if (bookingRepository.getReferenceById(bookingId).getId() != null) {
            Booking tempBooking = bookingRepository.getReferenceById(bookingId);

            if (approved) {
                tempBooking.setStatus(BookingStatus.APPROVED);

            } else {
                tempBooking.setStatus(BookingStatus.REJECTED);
            }
            bookingRepository.save(tempBooking);
            return BookingMapper.toBookingDto(tempBooking);
        } else {
            throw new BookingNotFoundException("Booking not found");
        }
    }

    public List<BookingDto> findBookingByIdAndStatus(BookingStatus status, Long id) {
        if (userRepository.existsById(id)) {
            if (status.equals(BookingStatus.ALL)) {
                List<Booking> list1 = bookingRepository.findBookingsByBookerId(id);
                list1.sort(Comparator.comparing(Booking::getStart));
                return BookingMapper.toBookingDtos(list1);
            } else if (status.equals(BookingStatus.CURRENT)) {
                List<Booking> list2 = bookingRepository.findBookingsByBookerIdWithCurrentStatus(id);
                list2.sort(Comparator.comparing(Booking::getStart));
                return BookingMapper.toBookingDtos(list2);
            } else if (status.equals(BookingStatus.PAST)) {
                List<Booking> list3 = bookingRepository.findBookingsByBookerIdWithPastStatus(id);
                list3.sort(Comparator.comparing(Booking::getStart));
                return BookingMapper.toBookingDtos(list3);
            } else if (status.equals(BookingStatus.FUTURE)) {
                List<Booking> list4 = bookingRepository.findBookingsByBookerIdWithFutureStatus(id);
                list4.sort(Comparator.comparing(Booking::getStart));
                return BookingMapper.toBookingDtos(list4);
            } else if (status.equals(BookingStatus.WAITING)) {
                List<Booking> list5 = bookingRepository.findBookingsByBookerIdWithWaitingOrRejectStatus(id,
                        BookingStatus.WAITING);
                list5.sort(Comparator.comparing(Booking::getStart));
                return BookingMapper.toBookingDtos(list5);
            } else if (status.equals(BookingStatus.REJECTED)) {
                List<Booking> list6 = bookingRepository.findBookingsByBookerIdWithWaitingOrRejectStatus(id,
                        BookingStatus.REJECTED);
                list6.sort(Comparator.comparing(Booking::getStart));
                return BookingMapper.toBookingDtos(list6);
            }
        }
        throw new UserNotFoundException("User not found");
    }

    public List<BookingDto> findAllOwnersBookings(BookingStatus status, Long id) {
        if (userRepository.existsById(id)) {
            if (status != BookingStatus.ALL && status != BookingStatus.REJECTED && status != BookingStatus.WAITING
                    && status != BookingStatus.FUTURE && status != BookingStatus.CURRENT
                    && status != BookingStatus.APPROVED && status != BookingStatus.CANCELED
                    && status != BookingStatus.PAST) {
                throw new InvalidParameterException("UNSUPPORTED_STATUS");
            }
            if (status.equals(BookingStatus.ALL)) {
                return BookingMapper.toBookingDtos(bookingRepository.findAllOwnersBookings(id, BookingStatus.ALL));
            } else if (status.equals(BookingStatus.CURRENT)) {
                return BookingMapper.toBookingDtos(bookingRepository.findAllOwnersBookings(id, BookingStatus.CURRENT));
            } else if (status.equals(BookingStatus.PAST)) {
                return BookingMapper.toBookingDtos(bookingRepository.findAllOwnersBookings(id, BookingStatus.PAST));
            } else if (status.equals(BookingStatus.FUTURE)) {
                return BookingMapper.toBookingDtos(bookingRepository.findAllOwnersBookings(id, BookingStatus.FUTURE));
            } else if (status.equals(BookingStatus.WAITING)) {
                return BookingMapper.toBookingDtos(bookingRepository.findAllOwnersBookings(id, BookingStatus.WAITING));
            } else if (status.equals(BookingStatus.REJECTED)) {
                return BookingMapper.toBookingDtos(bookingRepository.findAllOwnersBookings(id, BookingStatus.REJECTED));
            }
        }
        throw new UserNotFoundException("User not found");

    }
}
