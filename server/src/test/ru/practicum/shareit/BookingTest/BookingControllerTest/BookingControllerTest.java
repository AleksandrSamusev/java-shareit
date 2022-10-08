package ru.practicum.shareit.BookingTest.BookingControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingServiceImpl bookingService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void createBookingTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.of(2000, 12, 31, 15, 30),
                LocalDateTime.of(2001, 12, 31, 15, 30),
                BookingStatus.WAITING,
                new UserDto(),
                1L,
                new ItemDto(),
                "item");

        when(bookingService.create(anyLong(), any()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void findBookingByIdTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.of(2000, 12, 31, 15, 30),
                LocalDateTime.of(2001, 12, 31, 15, 30),
                BookingStatus.WAITING,
                new UserDto(),
                1L,
                new ItemDto(),
                "item");

        when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

    }

    @Test
    public void testFindBookingWithBadId() throws Exception {
        when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenThrow(BookingNotFoundException.class);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findAllBookingsByIdTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.of(2023, 12, 31, 15, 30),
                LocalDateTime.of(2024, 12, 31, 15, 30),
                BookingStatus.WAITING,
                new UserDto(1L, "ivan", "ivan@ivan.com"),
                1L,
                new ItemDto(1L, "Wrench"),
                "Wrench");

        BookingDto bookingDto2 = new BookingDto(2L,
                LocalDateTime.of(2025, 12, 31, 15, 30),
                LocalDateTime.of(2026, 12, 31, 15, 30),
                BookingStatus.WAITING,
                new UserDto(1L, "ivan", "ivan@ivan.com"),
                1L,
                new ItemDto(2L, "Screwdriver"),
                "Screwdriver");

        List<BookingDto> list = new ArrayList<>();
        list.add(bookingDto);
        list.add(bookingDto2);

        Mockito.when(bookingService.findBookingByIdAndStatus(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(list);

        mvc.perform(get("/bookings?state=WAITING")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());

    }

    @Test
    public void confirmOrRejectBookingTest() throws Exception {

        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.of(2023, 12, 31, 15, 30),
                LocalDateTime.of(2024, 12, 31, 15, 30),
                BookingStatus.REJECTED,
                new UserDto(1L, "ivan", "ivan@ivan.com"),
                1L,
                new ItemDto(1L, "Wrench"),
                "Wrench");

        Mockito.when(bookingService.confirmOrRejectBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1?approved=false")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    public void findAllOwnersBookings() throws Exception {
        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.of(2023, 12, 31, 15, 30),
                LocalDateTime.of(2024, 12, 31, 15, 30),
                BookingStatus.WAITING,
                new UserDto(1L, "ivan", "ivan@ivan.com"),
                1L,
                new ItemDto(1L, "Wrench"),
                "Wrench");

        BookingDto bookingDto2 = new BookingDto(2L,
                LocalDateTime.of(2025, 12, 31, 15, 30),
                LocalDateTime.of(2026, 12, 31, 15, 30),
                BookingStatus.WAITING,
                new UserDto(1L, "ivan", "ivan@ivan.com"),
                1L,
                new ItemDto(2L, "Screwdriver"),
                "Screwdriver");

        List<BookingDto> list = new ArrayList<>();
        list.add(bookingDto);
        list.add(bookingDto2);

        Mockito.when(bookingService.findAllOwnersBookings(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(list);

        mvc.perform(get("/bookings/owner?state=WAITING")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());

    }

}