package ru.practicum.shareit.item;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.user.UserDto;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<CommentDto> comments = new HashSet<>();

    public ItemDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
