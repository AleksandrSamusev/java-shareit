package ru.practicum.shareit.item;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;

@Data
@NoArgsConstructor
public class ItemDtoBooking {
    private Long id;
    private String name;
    private String description;
    @JsonProperty(value = "available")
    private Boolean isAvailable;
    private Booking lastBooking;
    private Booking nextBooking;
}

