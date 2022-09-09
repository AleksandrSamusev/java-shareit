package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
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

    public Booking create(Long id, Booking booking) {
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new InvalidParameterException("End date in past");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new InvalidParameterException("End date before start");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new InvalidParameterException("Start date in past");
        }
        if (itemRepository.findById(booking.getItem().getId()).get().getId()==null) {
            throw new ItemNotFoundException("Item not found");
        }
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        if (itemRepository.findById(booking.getItem().getId()).isPresent()) {
            if (itemRepository.findById(booking.getItem().getId()).get().getIsAvailable() == Boolean.FALSE) {
                throw new InvalidParameterException("Item is unavailable");
            }
        }

        booking.setBooker(userRepository.findById(id).get());
        return bookingRepository.save(booking);
    }

    public BookingDto findBookingById(Long id, Long bookingId) {
        if (bookingRepository.findById(bookingId).isPresent()) {
            Booking tempBooking = bookingRepository.findById(bookingId).get();
            Item tempItem = itemRepository.findById(bookingRepository.findById(bookingId).get().getItem().getId()).get();
            User tempBooker = userRepository.findById(bookingRepository.findById(bookingId).get().getBooker().getId()).get();
            return BookingMapper.toBookingDto(tempBooking, tempItem, tempBooker);
        } else {
            throw new BookingNotFoundException("Booking not found");
        }
    }

    public BookingDto confirmOrRejectBooking(Long id, Long bookingId, Boolean approved) {

        if (bookingRepository.findById(bookingId).isPresent()) {
            Booking tempBooking = bookingRepository.findById(bookingId).get();

            if (approved) {
                tempBooking.setStatus(BookingStatus.APPROVED);
            } else {
                tempBooking.setStatus(BookingStatus.REJECTED);
            }
            bookingRepository.save(tempBooking);
            Item tempItem = itemRepository.findById(bookingRepository.findById(bookingId).get().getItem().getId()).get();
            User tempBooker = userRepository.findById(bookingRepository.findById(bookingId).get().getBooker().getId()).get();
            return BookingMapper.toBookingDto(tempBooking, tempItem, tempBooker);
        } else {
            throw new BookingNotFoundException("Booking not found");
        }
    }

    public List<Booking> findBookingByIdAndStatus(BookingStatus status, Long id) {
        if (userRepository.existsById(id)) {
            if (status.equals(BookingStatus.ALL)) {
                List<Booking> list1 = bookingRepository.findBookingsByBookerId(id);
                list1.sort(Comparator.comparing(Booking::getStart));
                return list1;
            } else if (status.equals(BookingStatus.CURRENT)) {
                List<Booking> list2 = bookingRepository.findBookingsByBookerIdWithCurrentStatus(id);
                list2.sort(Comparator.comparing(Booking::getStart));
                return list2;
            } else if (status.equals(BookingStatus.PAST)) {
                List<Booking> list3 = bookingRepository.findBookingsByBookerIdWithPastStatus(id);
                list3.sort(Comparator.comparing(Booking::getStart));
                return list3;
            } else if (status.equals(BookingStatus.FUTURE)) {
                List<Booking> list4 = bookingRepository.findBookingsByBookerIdWithFutureStatus(id);
                list4.sort(Comparator.comparing(Booking::getStart));
                return list4;
            } else if (status.equals(BookingStatus.WAITING)) {
                List<Booking> list5 = bookingRepository.findBookingsByBookerIdWithWaitingOrRejectStatus(id,
                        BookingStatus.WAITING);
                list5.sort(Comparator.comparing(Booking::getStart));
                return list5;
            } else if (status.equals(BookingStatus.REJECTED)) {
                List<Booking> list6 = bookingRepository.findBookingsByBookerIdWithWaitingOrRejectStatus(id,
                        BookingStatus.REJECTED);
                list6.sort(Comparator.comparing(Booking::getStart));
                return list6;
            }
        }
        throw new UserNotFoundException("User not found");
    }
}
