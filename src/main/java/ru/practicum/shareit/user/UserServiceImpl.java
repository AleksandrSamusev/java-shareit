package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        if (userRepository.findById(id).isPresent()) {
            return userRepository.findById(id);
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    public User createUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new InvalidParameterException("email is null");
        }
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        User temp = userRepository.getReferenceById(user.getId());
        if (user.getName() != null && !user.getName().equals("")) {
            temp.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().equals("")) {
            temp.setEmail(user.getEmail());
        }
        return userRepository.save(temp);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User patchUser(User user, Long userId) {
        user.setId(userId);
        if (findUserById(user.getId()).isPresent()) {
            User patchedUser = findUserById(user.getId()).get();
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
        } else {
            throw new UserNotFoundException("User not found");
        }
    }


}
