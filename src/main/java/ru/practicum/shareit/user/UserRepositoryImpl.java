package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAllUsers() {
        List<User> userList = new ArrayList<>();
        userList.addAll(users.values());
        return userList;
    }

    @Override
    public User createUser(User user) {
        if (!users.containsValue(user) && emailValidation(user)) {
            user.setId(UserIdGenerator.generateId());
            users.put(user.getId(), user);
        }
        log.info("Добавлен пользователь, id = \"{}\"", user.getId());
        return user;
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("Пользователь с id = \"{}\" удален", id);
        users.remove(id);
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() != null && users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else if (users.containsValue(user)) {
            Long id = users.get(user.getId()).getId();
            user.setId(id);
            users.put(id, user);
        }
        log.info("Пользователь с id = \"{}\" обновлен", user.getId());
        return user;
    }

    @Override
    public User findUserById(Long id) {
        for (User user : findAllUsers()) {
            if (user.getId().equals(id)) {
                log.info("Вернулся пользователь c id = \"{}\"", user.getId());
                return user;
            }
        }
        return null;
    }

    private boolean emailValidation(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new InvalidParameterException("email is null");
        }
        for (User storedUser : users.values()) {
            if (user.getEmail().equals(storedUser.getEmail())) {
                log.info("Пользователь с email: \"{}\" уже зарегистрирован", user.getEmail());
                throw new ValidationException("Пользователь уже зарегистрирован");
            }
            log.info("Успешная валидация");
        }
        return true;
    }
}
