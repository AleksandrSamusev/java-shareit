package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotNull;

@Data
@Component
public class Item {
    private Long id;
    private String name;
    private String description;
    @NotNull
    @JsonProperty(value = "available")
    private Boolean isAvailable;
    private Long owner;
    private ItemRequest request;


}


