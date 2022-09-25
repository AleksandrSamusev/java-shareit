package ru.practicum.shareit.RequestTest.ServiceTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@SpringBootTest(
        properties = "db.name=shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

public class RequestServiceTest<T extends RequestService> {

    private final EntityManager em;
    private final RequestService requestService;
    private final UserService userService;
    private final ItemService itemService;

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
    public void createRequestCreateWithBadRequestDescriptionTest() {
        UserDto userDto = new UserDto();
        userDto.setName("Ivan");
        userDto.setEmail("Ivan@ivan.ru");

        userService.createUser(userDto);


        assertThrows(InvalidParameterException.class,
                () -> requestService.createRequest(1L, new RequestDto()));
    }

    @Test
    public void createRequestCreateWithBadUserTest() {
        UserDto userDto = new UserDto();
        userDto.setName("Ivan");
        userDto.setEmail("Ivan@ivan.ru");
        userService.createUser(userDto);

        RequestDto dto = new RequestDto();
        dto.setCreated(LocalDateTime.now());
        dto.setRequestor(userDto);
        dto.setDescription("I need screwdriver");
        requestService.createRequest(1L, dto);

        assertThrows(UserNotFoundException.class,
                () -> requestService.createRequest(999L, dto));

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
        request.setRequestor(user2);
        request.setDescription("Screwdriver needed");
        request.setCreated(LocalDateTime.now());
        requestService.createRequest(2L, RequestMapper.toRequestDto(request));

        Item item = new Item();
        item.setRequestId(1L);
        item.setIsAvailable(true);
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");
        itemService.createItem(1L, 1L, ItemMapper.toItemDto(item));

        TypedQuery<Request> query = em.createQuery("SELECT r FROM Request r WHERE r.id IN" +
                " (SELECT i FROM Item i WHERE i.requestId = :id)", Request.class);

        List<RequestDtoResponse> responce = requestService.findAllRequestsWithResponses(2L);

        assertThat(responce.size(), equalTo(1));
        assertThrows(UserNotFoundException.class,
                () -> requestService.findAllRequestsWithResponses(-1L));

    }

    @Test
    public void findRequestWithResponses() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@user2.ru");
        userService.createUser(UserMapper.toUserDto(user2));

        Request request = new Request();
        request.setRequestor(user2);
        request.setDescription("Screwdriver needed");
        request.setCreated(LocalDateTime.now());
        requestService.createRequest(2L, RequestMapper.toRequestDto(request));

        Item item = new Item();
        item.setRequestId(1L);
        item.setIsAvailable(true);
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");
        itemService.createItem(1L, 1L, ItemMapper.toItemDto(item));

        TypedQuery<Request> query = em.createQuery("SELECT r FROM Request r WHERE r.id IN" +
                " (SELECT i FROM Item i WHERE i.requestId = :id)", Request.class);
        RequestDtoResponse responce = requestService.findRequestWithResponses(2L, 1L);

        assertThat(responce.getItems().get(0).getName(), equalTo("Screwdriver"));
        assertThrows(UserNotFoundException.class,
                () -> requestService.findRequestWithResponses(-1L, 1L));
        assertThrows(RequestNotFoundException.class,
                () -> requestService.findRequestWithResponses(2L, -1L));
    }


    @Test
    public void findAllRequestsWithPagination() {

        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        Request request = new Request();
        request.setRequestor(user);
        request.setDescription("Screwdriver needed");
        request.setCreated(LocalDateTime.now());
        requestService.createRequest(1L, RequestMapper.toRequestDto(request));

        assertThrows(UserNotFoundException.class,
                () -> requestService.findAllRequestsWithPagination(999L, 1, 1));
        assertThrows(InvalidParameterException.class,
                () -> requestService.findAllRequestsWithPagination(1L, -999, 1));
        assertThrows(InvalidParameterException.class,
                () -> requestService.findAllRequestsWithPagination(1L, 1, -999));

    }


}
