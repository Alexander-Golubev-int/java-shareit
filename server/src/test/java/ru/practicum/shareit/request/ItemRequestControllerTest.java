package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.constans.Constants;
import ru.practicum.shareit.item.dto.mappers.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto requestDto;
    private NewItemRequest newItemRequest;

    @BeforeEach
    void setUp() {
        requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Очень нужна дрель");
        requestDto.setCreated(LocalDateTime.now());

        ItemShortDto itemShortDto = new ItemShortDto();
        itemShortDto.setId(1L);
        itemShortDto.setName("Дрель");
        itemShortDto.setOwnerId(1L);
        requestDto.setItems(List.of(itemShortDto));

        newItemRequest = new NewItemRequest();
        newItemRequest.setDescription("Очень нужна дрель");
    }

    @Test
    void createRequest_ShouldReturnItemRequest() throws Exception {
        when(itemRequestService.createRequest(eq(1L), any(NewItemRequest.class)))
                .thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(newItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Очень нужна дрель"));

        verify(itemRequestService).createRequest(eq(1L), any(NewItemRequest.class));
    }

    @Test
    void getItemRequest_ShouldReturnListOfUserRequests() throws Exception {
        when(itemRequestService.getRequests(eq(1L)))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Очень нужна дрель"));

        verify(itemRequestService).getRequests(1L);
    }

    @Test
    void getAllItemRequest_ShouldReturnAllRequests() throws Exception {
        when(itemRequestService.getAllRequests())
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Очень нужна дрель"));

        verify(itemRequestService).getAllRequests();
    }

    @Test
    void getItemRequestById_ShouldReturnRequest() throws Exception {
        when(itemRequestService.getItemRequestById(eq(1L)))
                .thenReturn(requestDto);

        mockMvc.perform(get("/requests/{requestId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Очень нужна дрель"))
                .andExpect(jsonPath("$.items[0].name").value("Дрель"));

        verify(itemRequestService).getItemRequestById(1L);
    }
}