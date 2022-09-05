package ru.practicum.shareit.booking;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Component
public class BookingWithItemNameDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status = BookingStatus.WAITING;
    private User booker;
    private Item item;
    private String itemName;
}
