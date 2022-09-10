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

    public List<UserDto> findAllUsers() {
        return UserMapper.toUserDtos(userRepository.findAll());
    }

    public UserDto findUserById(Long id) {
        if (userRepository.getReferenceById(id).getId() != null) {
            if (userRepository.existsById(id)) {
                return UserMapper.toUserDto(userRepository.getReferenceById(id));
            } else {
                throw  new UserNotFoundException("User not found");
            }
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null || !userDto.getEmail().contains("@")) {
            throw new InvalidParameterException("email is null");
        }
/*        for (UserDto user : findAllUsers()) {
            if (user.getEmail().equals(userDto.getEmail())) {
                throw new InvalidParameterException("User already registered");
            }
        }*/
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
        if (findUserById(userDto.getId()) != null) {
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
        } else {
            throw new UserNotFoundException("User not found");
        }
    }


}
