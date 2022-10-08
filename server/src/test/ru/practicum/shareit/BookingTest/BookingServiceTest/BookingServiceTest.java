package ru.practicum.shareit.BookingTest.BookingServiceTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestDto;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.RequestService;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name = shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

public class BookingServiceTest<T extends BookingService> {

    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final RequestService requestService;

    @Test
    public void bookingCreateTest() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@user2.ru");
        userService.createUser(UserMapper.toUserDto(user2));

        Item item = new Item();
        item.setIsAvailable(true);
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");
        itemService.createItem(1L, null, ItemMapper.toItemDto(item));

        BookingSmallDto dto = new BookingSmallDto();
        dto.setStart(LocalDateTime.of(2023, 12, 31, 12, 30));
        dto.setEnd(LocalDateTime.of(2024, 12, 31, 12, 30));
        dto.setItemId(1L);
        bookingService.create(2L, dto);

        BookingSmallDto dtoWithBadItem = new BookingSmallDto();
        dtoWithBadItem.setItemId(999L);

        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.booker.name " +
                "= :name", Booking.class);
        Booking booking = query.setParameter("name", user2.getName()).getSingleResult();

        assertThat(booking.getBooker().getName(), equalTo("user2"));

        assertThrows(ItemNotFoundException.class,
                () -> bookingService.create(1L, dtoWithBadItem));
    }

    @Test
    public void givenUserIdAndBooking_whenCreateBooking_thenException() {

        UserDto user = new UserDto(null, "user", "user@user.ru");
        userService.createUser(user);

        UserDto user2 = new UserDto(null, "user2", "user2@user2.ru");
        userService.createUser(user2);

        RequestDto request = new RequestDto(null, "need screwdriver", user2, LocalDateTime.now());
        requestService.createRequest(2L, request);

        ItemDto item = new ItemDto(null, "Screwdriver",
                "This is the best screwdriver in the world!", true, user,
                1L, null, null, null);
        itemService.createItem(1L, 1L, item);

        BookingSmallDto booking = new BookingSmallDto(null,
                LocalDateTime.of(2023, 12, 12, 11, 12),
                LocalDateTime.of(2023, 12, 12, 12, 12), 1L);

        assertThrows(ItemNotFoundException.class,
                () -> bookingService.create(1L, booking));

    }

    @Test
    public void givenUserIdAndBooking_whenEndInPast_thenException() {
        UserDto user = new UserDto(null, "user", "user@user.ru");
        userService.createUser(user);

        UserDto user2 = new UserDto(null, "user2", "user2@user2.ru");
        userService.createUser(user2);

        RequestDto request = new RequestDto(null, "need screwdriver", user2, LocalDateTime.now());
        requestService.createRequest(2L, request);

        ItemDto item = new ItemDto(null, "Screwdriver",
                "This is the best screwdriver in the world!", true, user,
                1L, null, null, null);
        itemService.createItem(1L, 1L, item);
    }

    @Test
    public void givenUserIdAndBooking_whenEndBeforeStart_thenException() {
        UserDto user = new UserDto(null, "user", "user@user.ru");
        userService.createUser(user);

        UserDto user2 = new UserDto(null, "user2", "user2@user2.ru");
        userService.createUser(user2);

        RequestDto request = new RequestDto(null, "need screwdriver", user2, LocalDateTime.now());
        requestService.createRequest(2L, request);

        ItemDto item = new ItemDto(null, "Screwdriver",
                "This is the best screwdriver in the world!", true, user,
                1L, null, null, null);
        itemService.createItem(1L, 1L, item);
    }

    @Test
    public void givenUserIdAndBooking_whenStartInPast_thenException() {
        UserDto user = new UserDto(null, "user", "user@user.ru");
        userService.createUser(user);

        UserDto user2 = new UserDto(null, "user2", "user2@user2.ru");
        userService.createUser(user2);

        RequestDto request = new RequestDto(null, "need screwdriver", user2, LocalDateTime.now());
        requestService.createRequest(2L, request);

        ItemDto item = new ItemDto(null, "Screwdriver",
                "This is the best screwdriver in the world!", true, user,
                1L, null, null, null);
        itemService.createItem(1L, 1L, item);

    }

    @Test
    public void givenUserIdAndBooking_whenUserNotExists_thenException() {
        UserDto user = new UserDto(null, "user", "user@user.ru");
        userService.createUser(user);

        UserDto user2 = new UserDto(null, "user2", "user2@user2.ru");
        userService.createUser(user2);

        RequestDto request = new RequestDto(null, "need screwdriver", user2, LocalDateTime.now());
        requestService.createRequest(2L, request);

        ItemDto item = new ItemDto(null, "Screwdriver",
                "This is the best screwdriver in the world!", true, user,
                1L, null, null, null);
        itemService.createItem(1L, 1L, item);

        BookingSmallDto booking = new BookingSmallDto(null,
                LocalDateTime.of(2023, 12, 12, 11, 12),
                LocalDateTime.of(2023, 12, 12, 12, 12), 1L);

        assertThrows(UserNotFoundException.class,
                () -> bookingService.create(3L, booking));

    }

    @Test
    public void testCreateBadBookingWithFalseStatus() {
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
        request.setDescription("need screwdriver");
        request.setCreated(LocalDateTime.now());
        requestService.createRequest(2L, RequestMapper.toRequestDto(request));

        Item item = new Item();
        item.setIsAvailable(false);
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");
        itemService.createItem(1L, 1L, ItemMapper.toItemDto(item));

        BookingSmallDto booking = new BookingSmallDto();
        booking.setItemId(1L);
        booking.setStart(LocalDateTime.of(2023, 12, 12, 11, 12));
        booking.setEnd(LocalDateTime.of(2023, 12, 12, 12, 12));

        assertThrows(InvalidParameterException.class,
                () -> bookingService.create(2L, booking));

    }

    @Test
    public void findBookingByIdTest() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@user2.ru");
        userService.createUser(UserMapper.toUserDto(user2));

        Item item = new Item();
        item.setIsAvailable(true);
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");
        itemService.createItem(1L, null, ItemMapper.toItemDto(item));

        BookingSmallDto dto = new BookingSmallDto();
        dto.setStart(LocalDateTime.of(2023, 12, 31, 12, 30));
        dto.setEnd(LocalDateTime.of(2024, 12, 31, 12, 30));
        dto.setItemId(1L);
        bookingService.create(2L, dto);

        BookingDto bookingDto = bookingService.findBookingById(1L, 1L);
        assertThat(bookingDto.getId(), equalTo(1L));
        assertThrows(BookingNotFoundException.class,
                () -> bookingService.findBookingById(1L, 999L));
    }

    @Test
    public void confirmOrRejectBookingTest() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@user2.ru");
        userService.createUser(UserMapper.toUserDto(user2));

        Item item = new Item();
        item.setIsAvailable(true);
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");
        itemService.createItem(1L, null, ItemMapper.toItemDto(item));

        BookingSmallDto dto = new BookingSmallDto();
        dto.setStart(LocalDateTime.of(2023, 12, 31, 12, 30));
        dto.setEnd(LocalDateTime.of(2024, 12, 31, 12, 30));
        dto.setItemId(1L);

        bookingService.create(2L, dto);

        bookingService.confirmOrRejectBooking(1L, 1L, true);

        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.booker.name " +
                "= :name", Booking.class);
        Booking booking = query.setParameter("name", user2.getName()).getSingleResult();

        assertThat(booking.getStatus(), equalTo(BookingStatus.APPROVED));
        assertThrows(InvalidParameterException.class,
                () -> bookingService.confirmOrRejectBooking(1L, 1L, true));
        assertThrows(BookingNotFoundException.class,
                () -> bookingService.confirmOrRejectBooking(2L, 1L, true));
        assertThat(bookingService.confirmOrRejectBooking(1L, 1L, false).getStatus(),
                equalTo(BookingStatus.REJECTED));
    }

}