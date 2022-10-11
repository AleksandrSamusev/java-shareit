package ru.practicum.shareit.RequestTest.RequestControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.RequestDto;
import ru.practicum.shareit.request.RequestDtoResponse;
import ru.practicum.shareit.request.RequestServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RequestServiceImpl requestService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void createRequestTest() throws Exception {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("need something");

        when(requestService.createRequest(1L, requestDto))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.description").value("need something"));
    }

    @Test
    public void findAllRequestsWithResponses() throws Exception {
        RequestDtoResponse requestDto = new RequestDtoResponse();
        requestDto.setId(1L);
        requestDto.setDescription("need something");

        RequestDtoResponse requestDto2 = new RequestDtoResponse();
        requestDto.setId(2L);
        requestDto.setDescription("need something else");

        List<RequestDtoResponse> list = new ArrayList<>();
        list.add(requestDto);
        list.add(requestDto2);

        Mockito.when(requestService.findAllRequestsWithResponses(1L)).thenReturn(list);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(list)));
    }

    @Test
    public void testFindBadRequests() throws Exception {
        Mockito.when(requestService.findAllRequestsWithResponses(anyLong())).thenThrow(RequestNotFoundException.class);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindInvalidRequests() throws Exception {
        Mockito.when(requestService.findAllRequestsWithResponses(anyLong())).thenThrow(InvalidParameterException.class);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findRequestWithResponsesTest() throws Exception {
        RequestDtoResponse requestDto = new RequestDtoResponse();
        requestDto.setId(1L);
        requestDto.setDescription("need something");

        Mockito.when(requestService.findRequestWithResponses(1L, 1L)).thenReturn(requestDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.description").value("need something"));
    }

    @Test
    public void findAllRequestsWithPaginationTest() throws Exception {
        RequestDtoResponse requestDto = new RequestDtoResponse();
        requestDto.setId(1L);
        requestDto.setDescription("need something");

        RequestDtoResponse requestDto2 = new RequestDtoResponse();
        requestDto.setId(2L);
        requestDto.setDescription("need something else");

        List<RequestDtoResponse> list = new ArrayList<>();
        list.add(requestDto);
        list.add(requestDto2);

        Mockito.when(requestService.findAllRequestsWithPagination(anyLong(), anyInt(), anyInt())).thenReturn(list);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());

    }
}