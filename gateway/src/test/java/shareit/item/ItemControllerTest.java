package shareit.item;

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
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemClient itemClient;

    @InjectMocks
    private ItemController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void getItem_ShouldReturnOk() throws Exception {
        when(itemClient.getItemByID(eq(1L), eq(10L)))
                .thenReturn(ResponseEntity.ok("{\"id\":10,\"name\":\"Дрель\"}"));

        mvc.perform(get("/items/{itemId}", 10)
                        .header(Constants.USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("Дрель")));

        verify(itemClient, times(1)).getItemByID(eq(1L), eq(10L));
    }

    @Test
    void createItem_ShouldReturnOk() throws Exception {
        ItemRequestDto request = new ItemRequestDto();
        request.setName("Дрель");
        request.setDescription("Мощная");
        request.setAvailable(true);

        when(itemClient.createItem(eq(1L), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok("{\"id\":10,\"name\":\"Дрель\"}"));

        mvc.perform(post("/items")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("Дрель")));

        verify(itemClient, times(1)).createItem(eq(1L), any(ItemRequestDto.class));
    }

    @Test
    void createComment_ShouldReturnOk() throws Exception {
        NewCommentRequest request = new NewCommentRequest();
        request.setText("Отличная вещь!");

        when(itemClient.createComment(eq(1L), eq(10L), any(NewCommentRequest.class)))
                .thenReturn(ResponseEntity.ok("{\"id\":1,\"text\":\"Отличная вещь!\"}"));

        mvc.perform(post("/items/{itemId}/comment", 10)
                        .header(Constants.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is("Отличная вещь!")));

        verify(itemClient, times(1)).createComment(eq(1L), eq(10L), any(NewCommentRequest.class));
    }

    @Test
    void updateItem_ShouldReturnOk() throws Exception {
        UpdateItemRequest request = new UpdateItemRequest();
        request.setName("Новая дрель");

        when(itemClient.updateItem(eq(1L), eq(10L), any(UpdateItemRequest.class)))
                .thenReturn(ResponseEntity.ok("{\"id\":10,\"name\":\"Новая дрель\"}"));

        mvc.perform(patch("/items/{itemId}", 10)
                        .header(Constants.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Новая дрель")));

        verify(itemClient, times(1)).updateItem(eq(1L), eq(10L), any(UpdateItemRequest.class));
    }

    @Test
    void getAllItems_ShouldReturnOk() throws Exception {
        when(itemClient.getAllItems(eq(1L)))
                .thenReturn(ResponseEntity.ok("[{\"id\":10,\"name\":\"Дрель\"}]"));

        mvc.perform(get("/items")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(10)))
                .andExpect(jsonPath("$[0].name", is("Дрель")));

        verify(itemClient, times(1)).getAllItems(eq(1L));
    }

    @Test
    void getAllItemsBySearch_ShouldReturnOk() throws Exception {
        when(itemClient.getAllItemsBySearch(eq(1L), eq("дрель")))
                .thenReturn(ResponseEntity.ok("[{\"id\":10,\"name\":\"Дрель\"}]"));

        mvc.perform(get("/items/search")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .param("text", "дрель")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Дрель")));

        verify(itemClient, times(1)).getAllItemsBySearch(eq(1L), eq("дрель"));
    }
}