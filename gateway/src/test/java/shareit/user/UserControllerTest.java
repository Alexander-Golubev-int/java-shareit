package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserRequestDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void createUser_ShouldReturnOk() throws Exception {
        UserRequestDto request = new UserRequestDto();
        request.setName("Ivan");
        request.setEmail("ivan@yandex.ru");

        when(userClient.createUser(any(UserRequestDto.class)))
                .thenReturn(ResponseEntity.ok("{\"id\":1,\"name\":\"Ivan\",\"email\":\"ivan@yandex.ru\"}"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Ivan")))
                .andExpect(jsonPath("$.email", is("ivan@yandex.ru")));

        verify(userClient, times(1)).createUser(any(UserRequestDto.class));
    }

    @Test
    void updateUser_ShouldReturnOk() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Anton");
        request.setEmail("anton@yandex.ru");

        when(userClient.updateUser(eq(1L), any(UpdateUserRequest.class)))
                .thenReturn(ResponseEntity.ok("{\"id\":1,\"name\":\"Anton\",\"email\":\"anton@yandex.ru\"}"));

        mvc.perform(patch("/users/{id}", 1)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Anton")));

        verify(userClient, times(1)).updateUser(eq(1L), any(UpdateUserRequest.class));
    }

    @Test
    void getUserById_ShouldReturnOk() throws Exception {
        when(userClient.getUser(eq(1L)))
                .thenReturn(ResponseEntity.ok("{\"id\":1,\"name\":\"Ivan\",\"email\":\"ivan@yandex.ru\"}"));

        mvc.perform(get("/users/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Ivan")));

        verify(userClient, times(1)).getUser(eq(1L));
    }

    @Test
    void deleteUserById_ShouldReturnOk() throws Exception {
        when(userClient.deleteUser(eq(1L)))
                .thenReturn(ResponseEntity.ok("{\"message\":\"пользователь успешно удален\"}"));

        mvc.perform(delete("/users/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("пользователь успешно удален")));

        verify(userClient, times(1)).deleteUser(eq(1L));
    }
}