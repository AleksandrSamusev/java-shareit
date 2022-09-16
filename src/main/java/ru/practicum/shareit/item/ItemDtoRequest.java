package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDtoRequest {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    @JsonProperty(value = "available")
    private Boolean isAvailable;
    private Long requestId;
}
