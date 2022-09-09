package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import javax.validation.constraints.NotNull;

@Data
@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    @NotNull
    @JsonProperty(value = "available")
    private Boolean isAvailable;
    private UserDto owner;
    private Long requestId;
}
