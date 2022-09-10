package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ItemDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
    @NotNull
    @JsonProperty(value = "available")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isAvailable;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDto owner;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long requestId;
    private BookingDto lastBooking;
    private BookingDto nextBooking;

    public ItemDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
