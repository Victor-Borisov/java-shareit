package ru.practicum.shareit.itemrequesttests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerWithMockMvcTests {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void init() {
        itemRequestDto = ItemRequestDto
                .builder()
                .id(1L)
                .description("item request description")
                .build();
    }

    @Test
    void createTest() throws Exception {
        when(itemRequestService.create(anyLong(), any()))
                .thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                   .content(mapper.writeValueAsString(itemRequestDto))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header("X-Sharer-User-Id", 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(itemRequestDto)));
    }

    @Test
    void getAllByUserTest() throws Exception {
        when(itemRequestService.getAllByUser(anyLong()))
                .thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header("X-Sharer-User-Id", 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestDto))));
    }

    @Test
    void getAllTest() throws Exception {
        when(itemRequestService.getAll(anyInt(), anyInt(), anyLong()))
                .thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests/all")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header("X-Sharer-User-Id", 2L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestDto))));
    }

    @Test
    void getByIdTest() throws Exception {
        when(itemRequestService.getById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);
        mvc.perform(get("/requests/1")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header("X-Sharer-User-Id", 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(itemRequestDto)));
    }
}
