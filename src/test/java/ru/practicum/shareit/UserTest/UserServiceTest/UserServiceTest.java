package ru.practicum.shareit.UserTest.UserServiceTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@SpringBootTest(
        properties = "db.name=shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

public class UserServiceTest<T extends UserService> {

    private final EntityManager em;
    private final RequestService requestService;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    @Test
    public void findAllUsersTest() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@user2.ru");
        userService.createUser(UserMapper.toUserDto(user2));

        List<UserDto> list = userService.findAllUsers();

        assertThat(list.size(), equalTo(2));
    }

    @Test
    public void updateUserTest() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        List<UserDto> list = userService.findAllUsers();

        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getName(), equalTo("user"));

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("new name");
        updatedUser.setEmail("user@user.ru");
        userService.updateUser(UserMapper.toUserDto(updatedUser));

        list = userService.findAllUsers();

        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getName(), equalTo("new name"));
    }

    @Test
    public void deleteByIdTest() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        List<UserDto> list = userService.findAllUsers();
        assertThat(list.size(), equalTo(1));

        userService.deleteUserById(1L);

        list = userService.findAllUsers();
        assertThat(list.size(), equalTo(0));
        assertThrows(UserNotFoundException.class,
                () -> userService.findUserById(999L));

    }

    @Test
    public void patchUserTest() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        UserDto patchedUserWithBadEmail = new UserDto();
        patchedUserWithBadEmail.setName("patchedUser");
        patchedUserWithBadEmail.setEmail("user@user.ru");

        UserDto patchedUser = new UserDto();
        patchedUser.setName("patchedUser");
        patchedUser.setEmail("patchedUser@patchedUser.ru");

        assertThrows(UserNotFoundException.class,
                () -> userService.patchUser(patchedUser, 99L));
        assertThrows(ValidationException.class,
                () -> userService.patchUser(patchedUserWithBadEmail, 1L));

    }
}
