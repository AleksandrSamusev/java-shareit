package ru.practicum.shareit.booking;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;

@Data
@Component
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status = BookingStatus.WAITING;
    private UserDto bookerDto;
    private ItemDto itemDto;
    private String itemName;
}
