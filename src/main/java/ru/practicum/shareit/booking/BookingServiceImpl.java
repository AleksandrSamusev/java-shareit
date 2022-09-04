package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidParameterException;

import java.time.LocalDateTime;

@Service
public class BookingServiceImpl {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking create( Long id, Booking booking) {
        if(booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new InvalidParameterException("End date in past");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new InvalidParameterException("End date before start");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new InvalidParameterException("Start date in past");
        }
        booking.setBooker(id);
        return bookingRepository.save(booking);
    }

    public Booking findBookingById(Long id) {
        return bookingRepository.getReferenceById(id);
    }


}
