package ru.practicum.shareit.ItemTest.ServiceTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
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

public class ItemServiceTest<T extends ItemService> {

    private final EntityManager em;
    private final RequestService requestService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    public void createItemWithoutRequest() {

        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        Item item = new Item();
        item.setIsAvailable(true);
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");
        itemService.createItem(1L, null, ItemMapper.toItemDto(item));

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.owner.name = :name", Item.class);

        Item itemFromDb = query.setParameter("name", user.getName()).getSingleResult();

        assertThat(itemFromDb.getId(), notNullValue());
        assertThat(itemFromDb.getName(), equalTo(item.getName()));
        assertThat(itemFromDb.getDescription(), equalTo(item.getDescription()));
    }

    @Test
    public void createItemWithoutRequestWithBadItemName() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        Item item = new Item();
        item.setIsAvailable(true);
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");

        assertThrows(InvalidParameterException.class,
                ()->itemService.createItem(1L, null, ItemMapper.toItemDto(item)));

    }

    @Test
    public void createItemWithoutRequestWithBadItemDescription() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        Item item = new Item();
        item.setIsAvailable(true);
        item.setName("Screwdriver");
        item.setOwner(user);

        assertThrows(InvalidParameterException.class,
                ()->itemService.createItem(1L, null, ItemMapper.toItemDto(item)));

    }

    @Test
    public void createItemWithoutRequestWithBadItemIsAvailableParameter() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        Item item = new Item();
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");

        assertThrows(InvalidParameterException.class,
                ()->itemService.createItem(1L, null, ItemMapper.toItemDto(item)));

    }

    @Test
    public void createItemWithRequest() {

        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        Request request = new Request();
        request.setRequestor(user);
        request.setDescription("Screwdriver needed");
        request.setCreated(LocalDateTime.now());
        requestService.createRequest(1L, RequestMapper.toRequestDto(request));

        Item item = new Item();
        item.setRequestId(1L);
        item.setIsAvailable(true);
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");
        itemService.createItem(1L, 1L, ItemMapper.toItemDto(item));

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.owner.name = :name", Item.class);
        Item itemFromDb = query.setParameter("name", user.getName()).getSingleResult();

        assertThat(itemFromDb.getId(), notNullValue());
        assertThat(itemFromDb.getName(), equalTo(item.getName()));
        assertThat(itemFromDb.getDescription(), equalTo(item.getDescription()));
        assertThrows(RequestNotFoundException.class,
                ()->itemService.createItem(1L, 999L, ItemMapper.toItemDto(item)));
    }

    @Test
    public void updateItem() {
        UserDto user = new UserDto();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(user);

        ItemDto item = new ItemDto();
        item.setIsAvailable(true);
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");
        itemService.createItem(1L, null, item);

        ItemDto updatedItem = new ItemDto();
        updatedItem.setId(1L);
        updatedItem.setIsAvailable(true);
        updatedItem.setName("Screwdriver with changed name");
        updatedItem.setOwner(user);
        updatedItem.setDescription("This is the best screwdriver in the world!");
        itemService.updateItem(updatedItem);

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.owner.name = :name", Item.class);

        Item itemFromDb = query.setParameter("name", user.getName()).getSingleResult();

        assertThat(itemFromDb.getId(), notNullValue());
        assertThat(itemFromDb.getName(), equalTo(updatedItem.getName()));
        assertThat(itemFromDb.getRequestId(), equalTo(null));
    }

    }

