package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBooker(UserMapper.toUserDto(booking.getBooker()));
        bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        bookingDto.setItemName(booking.getItem().getName());

        return bookingDto;
    }

    public static Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(bookingDto.getStatus());
        booking.setBooker(UserMapper.toUser(bookingDto.getBooker()));
        booking.setItem(ItemMapper.toItem(bookingDto.getItem()));
        return booking;
    }

    public static BookingSmallDto toBookingSmallDto(Booking booking) {
        BookingSmallDto bookingSmallDto = new BookingSmallDto();

        bookingSmallDto.setId(booking.getId());
        bookingSmallDto.setStart(booking.getStart());
        bookingSmallDto.setEnd(booking.getEnd());
        bookingSmallDto.setItemId(booking.getItem().getId());

        return bookingSmallDto;
    }

    public static Booking toBookingFromSmallDto(BookingSmallDto bookingSmallDto) {
        Booking booking = new Booking();
        booking.setId(bookingSmallDto.getId());
        booking.setStart(bookingSmallDto.getStart());
        booking.setEnd(bookingSmallDto.getEnd());
        return booking;
    }

    public static List<BookingDto> toBookingDtos(List<Booking> bookings) {
        List<BookingDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(toBookingDto(booking));
        }
        return dtos;
    }

}
