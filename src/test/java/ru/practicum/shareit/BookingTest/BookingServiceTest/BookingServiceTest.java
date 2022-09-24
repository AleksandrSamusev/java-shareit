package ru.practicum.shareit.BookingTest.BookingServiceTest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.exception.*;
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

public class BookingServiceTest<T extends BookingService> {

    private final EntityManager em;
    private final RequestService requestService;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;


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
        dto.setStart(LocalDateTime.of(2023,12,31,12,30));
        dto.setEnd(LocalDateTime.of(2024,12,31,12,30));
        dto.setItemId(1L);

        bookingService.create(2L, dto);

        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.booker.name " +
                "= :name", Booking.class);
        Booking booking = query.setParameter("name", user2.getName()).getSingleResult();

        assertThat(booking.getBooker().getName(), equalTo("user2"));

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
        dto.setStart(LocalDateTime.of(2023,12,31,12,30));
        dto.setEnd(LocalDateTime.of(2024,12,31,12,30));
        dto.setItemId(1L);
        bookingService.create(2L, dto);

        BookingDto bookingDto = bookingService.findBookingById(1L, 1L);
        assertThat(bookingDto.getId(), equalTo(1L));
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
        dto.setStart(LocalDateTime.of(2023,12,31,12,30));
        dto.setEnd(LocalDateTime.of(2024,12,31,12,30));
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
