package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {

    List<User> findAllUsers();

    User createUser(User user);

    User updateUser(User user);

    void deleteUserById(Long id);

    User findUserById(Long id);
}
