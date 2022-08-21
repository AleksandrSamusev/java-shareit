package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }


    public User createUser(User user) {
        return userRepository.createUser(user);
    }


    public User updateUser(User user) {
        return userRepository.updateUser(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteUserById(id);
    }

    public User findUserById(Long id) {
        return userRepository.findUserById(id);
    }

    @Override
    public User patchUser(User user, Long userId) {
        user.setId(userId);
        User patchedUser = findUserById(user.getId());
        if (user.getName() != null) {
            patchedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            for (User storedUser : findAllUsers()) {
                if (user.getEmail().equals(storedUser.getEmail())) {
                    throw new ValidationException("Email уже есть в базе");
                }
            }
            patchedUser.setEmail(user.getEmail());
        }
        return patchedUser;
    }


}
