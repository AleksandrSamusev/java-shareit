package ru.practicum.shareit.user;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;

@Data
@Component
public class UserDto {
    private Long id;
    private String name;
    @Email
    private String email;
}
