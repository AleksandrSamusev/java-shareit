package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemDtoRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestDtoResponse {
    private Long id;
    private String description;
    private LocalDateTime created = LocalDateTime.now();
    private List<ItemDtoRequest> items = new ArrayList<>();
}
