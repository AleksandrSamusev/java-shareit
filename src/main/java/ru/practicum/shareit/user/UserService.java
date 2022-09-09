package ru.practicum.shareit.user;

public interface UserService {

    UserDto patchUser(UserDto userDto, Long userId);
}
