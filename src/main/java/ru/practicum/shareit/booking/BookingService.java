package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {

    BookingDto create(Long id, BookingSmallDto bookingSmallDto);

    BookingDto findBookingById(Long id, Long bookingId);

    BookingDto confirmOrRejectBooking(Long id, Long bookingId, Boolean approved);

    List<BookingDto> findBookingByIdAndStatus(String state, Long id, Integer from, Integer size);

    List<BookingDto> findAllOwnersBookings(String state, Long id, Integer from, Integer size);


}
