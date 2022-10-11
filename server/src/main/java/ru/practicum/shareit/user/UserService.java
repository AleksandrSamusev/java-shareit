package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    UserDto patchUser(UserDto userDto, Long userId);

    List<UserDto> findAllUsers();

    UserDto findUserById(Long id);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    void deleteUserById(Long id);


}
