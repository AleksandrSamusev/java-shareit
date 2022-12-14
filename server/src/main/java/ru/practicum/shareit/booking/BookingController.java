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
    public BookingDto saveBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                  @RequestBody BookingSmallDto bookingSmallDto) {
        return bookingService.create(id, bookingSmallDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader("X-Sharer-User-Id") Long id,
                                      @PathVariable Long bookingId) {
        return bookingService.findBookingById(id, bookingId);
    }

    @GetMapping
    public List<BookingDto> findAllBookingsById(@RequestParam(defaultValue = "ALL") String state,
                                                @RequestHeader("X-Sharer-User-Id") Long id,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        return bookingService.findBookingByIdAndStatus(state, id, from, size);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirmOrRejectBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                             @PathVariable Long bookingId,
                                             @RequestParam Boolean approved) {
        return bookingService.confirmOrRejectBooking(id, bookingId, approved);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllOwnersBookings(@RequestParam(defaultValue = "ALL") String state,
                                                  @RequestHeader("X-Sharer-User-Id") Long id,
                                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(required = false, defaultValue = "10") Integer size) {

        return bookingService.findAllOwnersBookings(state, id, from, size);
    }

}
