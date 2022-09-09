package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public BookingDto findBookingById(@RequestHeader("X-Sharer-User-Id") Long id,
                                      @PathVariable Long bookingId) {
        return bookingService.findBookingById(id, bookingId);
    }

    @GetMapping
    public List<Booking> findAllBookingsById(@RequestParam (defaultValue = "ALL") BookingStatus status,
                                             @RequestHeader("X-Sharer-User-Id") Long id) {

        return bookingService.findBookingByIdAndStatus(status, id);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirmOrRejectBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                             @PathVariable Long bookingId,
                                             @RequestParam Boolean approved) {
        return bookingService.confirmOrRejectBooking(id,bookingId, approved);
    }

    //@GetMapping("/owner")

}
