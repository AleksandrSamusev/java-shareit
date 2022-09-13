package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> findAllUsers() {
        return UserMapper.toUserDtos(userRepository.findAll());
    }

    public UserDto findUserById(Long id) {
        validateUser(id);
        return UserMapper.toUserDto(userRepository.getReferenceById(id));
    }

    public UserDto createUser(UserDto userDto) {
        validateEmail(userDto);
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
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(temp)));
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto patchUser(UserDto userDto, Long userId) {
        userDto.setId(userId);
        if (findUserById(userDto.getId()) == null) {
            throw new UserNotFoundException("User not found");
        }
        UserDto patchedUser = findUserById(userDto.getId());
        if (userDto.getName() != null) {
            patchedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            for (UserDto storedUser : findAllUsers()) {
                if (userDto.getEmail().equals(storedUser.getEmail())) {
                    throw new ValidationException("Email уже есть в базе");
                }
            }
            patchedUser.setEmail(userDto.getEmail());
        }
        return patchedUser;
    }

    private void validateUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
    }

    private void validateEmail(UserDto userDto) {
        if (userDto.getEmail() == null || !userDto.getEmail().contains("@")) {
            throw new InvalidParameterException("email is null");
        }
    }

}
