package ru.practicum.shareit.booking;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.entity.BookingSmallDto;

@RestController
@Slf4j
@RequestMapping("/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> saveBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                              @RequestBody BookingSmallDto bookingSmallDto) {
        return bookingClient.saveBooking(id, bookingSmallDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBookingById(@RequestHeader("X-Sharer-User-Id") Long id,
                                                  @PathVariable Long bookingId) {
        return bookingClient.findBookingById(id, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingsById(@RequestParam(defaultValue = "ALL") String state,
                                                      @RequestHeader("X-Sharer-User-Id") Long id,
                                                      @RequestParam(required = false, defaultValue = "0") Integer from,
                                                      @RequestParam(required = false, defaultValue = "10") Integer size) {
        return bookingClient.getBookings(id, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmOrRejectBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                                         @PathVariable Long bookingId,
                                                         @RequestParam Boolean approved) {
        return bookingClient.confirmOrRejectBooking(id, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllOwnersBookings(@RequestParam(defaultValue = "ALL") String state,
                                                        @RequestHeader("X-Sharer-User-Id") Long id,
                                                        @RequestParam(required = false,
                                                                defaultValue = "0") Integer from,
                                                        @RequestParam(required = false,
                                                                defaultValue = "10") Integer size) {

        return bookingClient.findAllOwnersBookings(state, id, from, size);
    }

}
