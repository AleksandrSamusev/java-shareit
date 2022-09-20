package ru.practicum.shareit.RequestTest.ServiceTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


@Transactional
@Rollback(value = false)
@SpringBootTest(
        properties = "db.name=shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest<T extends RequestService> {

    private final EntityManager em;
    private final RequestService requestService;
    private final UserService userService;

    @Test
    public void createRequestCreateTest() {
        UserDto userDto = new UserDto();
        userDto.setName("Ivan");
        userDto.setEmail("Ivan@ivan.ru");

        userService.createUser(userDto);

        RequestDto dto = new RequestDto();
        dto.setCreated(LocalDateTime.now());
        dto.setRequestor(userDto);
        dto.setDescription("I need screwdriver");

        requestService.createRequest(1L, dto);

        TypedQuery<Request> query = em.createQuery("SELECT r FROM Request r WHERE r.requestor.name = :name", Request.class);
        Request request = query.setParameter("name", dto.getRequestor().getName()).getSingleResult();

        assertThat(request.getId(), notNullValue());
        assertThat(request.getRequestor().getName(), equalTo(dto.getRequestor().getName()));
        assertThat(request.getRequestor().getEmail(), equalTo(dto.getRequestor().getEmail()));
        assertThat(request.getDescription(), equalTo(dto.getDescription()));
    }

    @Test
    public void findAllRequestsWithResponses() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@user2.ru");
        userService.createUser(UserMapper.toUserDto(user2));

        Request request = new Request();
        request.setRequestor(user);
        request.setDescription("Screwdriver needed");
        request.setCreated(LocalDateTime.now());
        requestService.createRequest(user.getId(), RequestMapper.toRequestDto(request));



    }

}
