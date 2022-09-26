package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RequestDto {
    private Long id;
    private String description;
    private UserDto requestor;
    private LocalDateTime created = LocalDateTime.now();}
