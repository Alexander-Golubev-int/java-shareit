package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.constans.Constants;
import ru.practicum.shareit.request.dto.RequestRequestDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

    @Mock
    private RequestClient requestClient;

    @InjectMocks
    private RequestController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void createRequest_ShouldReturnOk() throws Exception {
        RequestRequestDto requestDto = new RequestRequestDto();
        requestDto.setDescription("Очень нужна дрель");

        when(requestClient.createRequest(eq(1L), any(RequestRequestDto.class)))
                .thenReturn(ResponseEntity.ok("{\"id\":1,\"description\":\"Очень нужна дрель\"}"));

        mvc.perform(post("/requests")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Очень нужна дрель")));

        verify(requestClient, times(1)).createRequest(eq(1L), any(RequestRequestDto.class));
    }

    @Test
    void getItemRequest_ShouldReturnOk() throws Exception {
        when(requestClient.getRequest(eq(1L)))
                .thenReturn(ResponseEntity.ok("[{\"id\":1,\"description\":\"Очень нужна дрель\"}]"));

        mvc.perform(get("/requests")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Очень нужна дрель")));

        verify(requestClient, times(1)).getRequest(eq(1L));
    }

    @Test
    void getItemRequestAll_ShouldReturnOk() throws Exception {
        when(requestClient.getAllRequest())
                .thenReturn(ResponseEntity.ok("[{\"id\":2,\"description\":\"Нужна пила\"}]"));

        mvc.perform(get("/requests/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].description", is("Нужна пила")));

        verify(requestClient, times(1)).getAllRequest();
    }

    @Test
    void getItemRequestById_ShouldReturnOk() throws Exception {
        when(requestClient.getItemRequestById(eq(5L)))
                .thenReturn(ResponseEntity.ok("{\"id\":5,\"description\":\"Нужен молоток\"}"));

        mvc.perform(get("/requests/{requestId}", 5)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.description", is("Нужен молоток")));

        verify(requestClient, times(1)).getItemRequestById(eq(5L));
    }
}