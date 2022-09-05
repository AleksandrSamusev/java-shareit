package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
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
        if (!itemRepository.existsById(booking.getItemId())) {
            throw new ItemNotFoundException("Item not found");
        }
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        if (itemRepository.findById(booking.getItemId()).isPresent()) {
            if (itemRepository.findById(booking.getItemId()).get().getIsAvailable() == Boolean.FALSE) {
                throw new InvalidParameterException("Item is unavailable");
            }
        }
        booking.setBookerId(id);
        return bookingRepository.save(booking);
    }

    public BookingWithItemNameDto findBookingById(Long id, Long bookingId) {
/*        if (!id.equals(bookingRepository.findById(bookingId).get().getBookerId()) &&
                !id.equals(itemRepository.findById(bookingRepository.findById(bookingId).get().getItemId()).get().getOwnerId())) {
            throw new ValidationException("Information available only for booker or item owner.");
        }*/

        if (bookingRepository.findById(bookingId).isPresent()) {
            Booking tempBooking = bookingRepository.findById(bookingId).get();
            Item tempItem = itemRepository.findById(bookingRepository.findById(bookingId).get().getItemId()).get();
            User tempBooker = userRepository.findById(bookingRepository.findById(bookingId).get().getBookerId()).get();
            return BookingMapper.toBookingWithNameDto(tempBooking, tempItem, tempBooker);
        } else {
            throw new BookingNotFoundException("Booking not found");
        }
    }

    public BookingWithItemNameDto confirmOrRejectBooking(Long id, Long bookingId, Boolean approved) {

        if (bookingRepository.findById(bookingId).isPresent()) {
            Booking tempBooking = bookingRepository.findById(bookingId).get();

            if (approved) {
                tempBooking.setStatus(BookingStatus.APPROVED);
            } else {
                tempBooking.setStatus(BookingStatus.REJECTED);
            }
            bookingRepository.save(tempBooking);
            Item tempItem = itemRepository.findById(bookingRepository.findById(bookingId).get().getItemId()).get();
            User tempBooker = userRepository.findById(bookingRepository.findById(bookingId).get().getBookerId()).get();
            return BookingMapper.toBookingWithNameDto(tempBooking, tempItem, tempBooker);
        } else {
            throw new BookingNotFoundException("Booking not found");
        }
    }

    public List<Booking> findAllBookingById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        return bookingRepository.findAll();
    }
}
