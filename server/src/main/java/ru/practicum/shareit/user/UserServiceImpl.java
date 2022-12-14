package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> findAllUsers() {
        log.info("Returned list of Users");
        return UserMapper.toUserDtos(userRepository.findAll());
    }

    public UserDto findUserById(Long id) {
        validateUser(id);
        log.info("returned user with ID = {}", id);
        return UserMapper.toUserDto(userRepository.getReferenceById(id));
    }

    public UserDto createUser(UserDto userDto) {
        log.info("Created user");
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    public UserDto updateUser(UserDto userDto) {
        UserDto temp = UserMapper.toUserDto(userRepository.getReferenceById(userDto.getId()));
        if (userDto.getName() != null && !userDto.getName().equals("")) {
            temp.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals("")) {
            temp.setEmail(userDto.getEmail());
        }
        log.info("User with ID = {} was updated", userDto.getId());
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(temp)));
    }

    public void deleteUserById(Long id) {
        log.info("User with ID = {} was deleted", id);
        userRepository.deleteById(id);
    }

    @Override
    public UserDto patchUser(UserDto userDto, Long userId) {
        userDto.setId(userId);
        UserDto patchedUser = findUserById(userDto.getId());
        if (userDto.getName() != null) {
            patchedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            for (UserDto storedUser : findAllUsers()) {
                if (userDto.getEmail().equals(storedUser.getEmail())) {
                    log.info("ValidationException: Email ?????? ???????? ?? ????????");
                    throw new ValidationException("Email ?????? ???????? ?? ????????");
                }
            }
            patchedUser.setEmail(userDto.getEmail());
        }
        return patchedUser;
    }

    private void validateUser(Long id) {
        if (!userRepository.existsById(id)) {
            log.info("UserNotFoundException: User not found");
            throw new UserNotFoundException("User not found");
        }
    }

}
