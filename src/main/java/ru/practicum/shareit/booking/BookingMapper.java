package ru.practicum.shareit.booking;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@Component
public class BookingMapper {

    public static BookingWithItemNameDto toBookingWithNameDto(Booking booking, Item item, User booker) {
        BookingWithItemNameDto bookingWithItemNameDto = new BookingWithItemNameDto();

        bookingWithItemNameDto.setId(booking.getId());
        bookingWithItemNameDto.setStart(booking.getStart());
        bookingWithItemNameDto.setEnd(booking.getEnd());
        bookingWithItemNameDto.setStatus(booking.getStatus());
        bookingWithItemNameDto.setBooker(booker);
        bookingWithItemNameDto.setItem(item);
        bookingWithItemNameDto.setItemName(item.getName());

        return bookingWithItemNameDto;
    }




}
