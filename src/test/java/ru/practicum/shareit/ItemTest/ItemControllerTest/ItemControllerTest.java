package ru.practicum.shareit.ItemTest.ItemControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemServiceImpl itemService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void createNewItemTest() throws Exception {

        ItemDto itemDto = new ItemDto(1L, "Wrench");
        when(itemService.createItem(anyLong(), anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());

    }

    @Test
    void createItemWithException() throws Exception {

        ItemDto itemDto = new ItemDto(null, "Wrench");

        when(itemService.createItem(anyLong(), anyLong(), any()))
                .thenThrow(InvalidParameterException.class);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void patchItemTest() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Wrench");
        when(itemService.updateItem(itemService.patchItem(any(), anyLong(), anyLong())))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());
    }

    @Test
    public void getItemByIdTest() throws Exception {

        ItemDto item = new ItemDto(1L, "Wrench");

        Mockito.when(itemService.findItemById(anyLong(), anyLong())).thenReturn(item);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Wrench"));

    }

    @Test
    public void getWrongItemByIdTest() throws Exception {

        Mockito.when(itemService.findItemById(anyLong(), anyLong())).thenThrow(ItemNotFoundException.class);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());

    }


    @Test
    public void findItemByOwner() throws Exception {
        ItemDto item = new ItemDto(1L, "Wrench");
        ItemDto item2 = new ItemDto(2L, "Wrench2");

        List<ItemDto> list = new ArrayList<>();
        list.add(item);
        list.add(item2);

        Mockito.when(itemService.findAllItemsByOwner(anyLong())).thenReturn(list);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(list)));
    }

    @Test
    public void findItemByStringTest() throws Exception {
        ItemDto item = new ItemDto(1L, "Wrench");
        ItemDto item2 = new ItemDto(2L, "Wrench2");

        List<ItemDto> list = new ArrayList<>();
        list.add(item);
        list.add(item2);

        Mockito.when(itemService.getAllItemsByString(anyString())).thenReturn(list);

        mvc.perform(get("/items/search?text=Wrench"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(list)));
    }

    @Test
    public void createComment() throws Exception {

        CommentDto commentDto = new CommentDto();
        when(itemService.postComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
