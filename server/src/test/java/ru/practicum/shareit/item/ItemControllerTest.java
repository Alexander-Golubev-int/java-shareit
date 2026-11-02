package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.constans.Constants;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private ItemDtoDate itemDtoDate;
    private NewItemRequest newItemRequest;
    private UpdateItemRequest updateItemRequest;
    private NewCommentRequest newCommentRequest;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setDescription("Мощная");
        itemDto.setAvailable(true);

        itemDtoDate = new ItemDtoDate();
        itemDtoDate.setId(1L);
        itemDtoDate.setName("Дрель");
        itemDtoDate.setLastBooking(LocalDateTime.now().minusDays(1));
        itemDtoDate.setNextBooking(LocalDateTime.now().plusDays(1));

        newItemRequest = new NewItemRequest();
        newItemRequest.setName("Дрель");
        newItemRequest.setDescription("Мощная");
        newItemRequest.setAvailable(true);

        updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setName("Новая дрель");

        newCommentRequest = new NewCommentRequest();
        newCommentRequest.setText("Отличная вещь!");

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Отличная вещь!");
        commentDto.setAuthorName("Ivan");
    }

    @Test
    void createItem_ShouldReturnItem() throws Exception {
        when(itemService.createItem(eq(1L), any(NewItemRequest.class)))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(newItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"));

        verify(itemService).createItem(eq(1L), any(NewItemRequest.class));
    }

    @Test
    void createComment_ShouldReturnComment() throws Exception {
        when(itemService.createComment(eq(1L), eq(1L), any(NewCommentRequest.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(Constants.USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(newCommentRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Отличная вещь!"))
                .andExpect(jsonPath("$.authorName").value("Ivan"));

        verify(itemService).createComment(eq(1L), eq(1L), any(NewCommentRequest.class));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() throws Exception {
        itemDto.setName("Новая дрель");
        when(itemService.updateItem(eq(1L), eq(1L), any(UpdateItemRequest.class)))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header(Constants.USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(updateItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Новая дрель"));

        verify(itemService).updateItem(eq(1L), eq(1L), any(UpdateItemRequest.class));
    }

    @Test
    void getItem_ShouldReturnItemWithDates() throws Exception {
        when(itemService.getItemByID(eq(1L), eq(1L)))
                .thenReturn(itemDtoDate);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.lastBooking").exists())
                .andExpect(jsonPath("$.nextBooking").exists());

        verify(itemService).getItemByID(1L, 1L);
    }

    @Test
    void getAllItems_ShouldReturnList() throws Exception {
        when(itemService.getAllItems(eq(1L)))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"));

        verify(itemService).getAllItems(1L);
    }

    @Test
    void getAllItemsBySearch_ShouldReturnList() throws Exception {
        when(itemService.getAllItemsBySearch(eq(1L), eq("дрель")))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"));

        verify(itemService).getAllItemsBySearch(1L, "дрель");
    }
}