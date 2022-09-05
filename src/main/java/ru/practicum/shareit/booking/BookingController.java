package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/bookings")
public class BookingController {

    private final BookingServiceImpl bookingService;

    @Autowired
    public BookingController(BookingServiceImpl bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking saveBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                               @RequestBody Booking booking) {
        return bookingService.create(id, booking);
    }

    @GetMapping("/{bookingId}")
    public BookingWithItemNameDto findBookingById(@RequestHeader("X-Sharer-User-Id") Long id,
                                                  @PathVariable Long bookingId) {
        return bookingService.findBookingById(id, bookingId);
    }

    @GetMapping
    public List<Booking> findAllBookingsById(@RequestHeader("X-Sharer-User-Id") Long id) {
        return bookingService.findAllBookingById(id);
    }

    @PatchMapping("/{bookingId}")
    public BookingWithItemNameDto confirmOrRejectBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        return bookingService.confirmOrRejectBooking(id,bookingId, approved);
    }
}
