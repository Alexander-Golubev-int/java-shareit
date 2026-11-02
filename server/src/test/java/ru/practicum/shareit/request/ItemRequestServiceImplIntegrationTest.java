package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("User");
        user.setEmail("user@mail.ru");
        user = userRepository.save(user);
    }

    @Test
    void createRequest_ShouldCreate() {
        NewItemRequest request = new NewItemRequest();
        request.setDescription("Нужна дрель");

        ItemRequestDto result = itemRequestService.createRequest(user.getId(), request);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Нужна дрель");
    }

    @Test
    void getRequests_ShouldReturnUserRequests() {
        NewItemRequest request = new NewItemRequest();
        request.setDescription("Нужна дрель");
        itemRequestService.createRequest(user.getId(), request);

        List<ItemRequestDto> result = itemRequestService.getRequests(user.getId());

        assertThat(result).hasSize(1);
    }

    @Test
    void getRequests_WhenEmpty_ShouldReturnEmptyList() {
        List<ItemRequestDto> result = itemRequestService.getRequests(user.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void getAllRequests_ShouldReturnAll() {
        NewItemRequest request = new NewItemRequest();
        request.setDescription("Нужна дрель");
        itemRequestService.createRequest(user.getId(), request);

        List<ItemRequestDto> result = itemRequestService.getAllRequests();

        assertThat(result).isNotEmpty();
    }

    @Test
    void getItemRequestById_ShouldReturn() {
        NewItemRequest request = new NewItemRequest();
        request.setDescription("Нужна дрель");
        ItemRequestDto created = itemRequestService.createRequest(user.getId(), request);

        ItemRequestDto found = itemRequestService.getItemRequestById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
    }

    @Test
    void getItemRequestById_WhenNotFound_ShouldThrow() {
        assertThatThrownBy(() -> itemRequestService.getItemRequestById(999L))
                .isInstanceOf(NotFoundException.class);
    }
}