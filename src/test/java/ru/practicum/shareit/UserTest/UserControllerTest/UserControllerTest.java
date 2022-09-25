package ru.practicum.shareit.UserTest.UserControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserServiceImpl userService;

    @Autowired
    private MockMvc mvc;
    private UserDto userDto = new UserDto(1L, "John", "john@joseph.com");

    @Test
    void saveNewUser() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@joseph.com"));


    }

    @Test
    void updateUser() throws Exception {
        when(userService.updateUser(any()))
                .thenReturn(userDto);

        mvc.perform(patch("/users/{userId}", userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@joseph.com"));
    }

    @Test
    void getAllUsers() throws Exception {

        UserDto user1 = new UserDto();
        user1.setName("adam");
        user1.setEmail("adam@adam.com");

        UserDto user2 = new UserDto();
        user2.setName("adam2");
        user2.setEmail("adam2@adam.com");

        ArrayList<UserDto> list = new ArrayList<>();
        list.add(user1);
        list.add(user2);

        Mockito.when(userService.findAllUsers()).thenReturn(list);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(list)));

    }

    @Test
    void getUserById() throws Exception {

        UserDto user = new UserDto(1L, "adam", "adam@adam.ru");

        Mockito.when(userService.findUserById(Mockito.any())).thenReturn(user);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("adam"));

    }

    @Test
    public void deleteUserByIdTest() throws Exception {

        UserDto user = new UserDto(1L, "adam", "adam@adam.ru");
        Mockito.when(userService.findUserById(Mockito.any())).thenReturn(user);

        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}
