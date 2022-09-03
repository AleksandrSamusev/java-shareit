package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new InvalidParameterException("email is null");
        }
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        User temp = userRepository.getReferenceById(user.getId());
        if (user.getName()!=null && !user.getName().equals("")) {
            temp.setName(user.getName());
        }
        if(user.getEmail()!=null && !user.getEmail().equals("")) {
            temp.setEmail(user.getEmail());
        }
        return userRepository.save(temp);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public User findUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        return userRepository.getReferenceById(id);
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
