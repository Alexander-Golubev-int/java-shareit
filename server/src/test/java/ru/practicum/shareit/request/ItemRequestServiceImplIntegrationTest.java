package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("testUser@mail.ru");
        user = userRepository.save(user);
    }

    @Test
    void createRequest_ShouldSaveRequestToDatabase() {
        NewItemRequest newItemRequest = new NewItemRequest();
        newItemRequest.setDescription("Хочется макбук");

        ItemRequestDto result = itemRequestService.createRequest(user.getId(), newItemRequest);

        entityManager.flush();
        entityManager.clear();

        ItemRequest savedRequest = itemRequestRepository.findById(result.getId()).orElseThrow();
        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getDescription()).isEqualTo("Хочется макбук");
        assertThat(savedRequest.getCreated()).isNotNull();
        assertThat(savedRequest.getRequestor().getId()).isEqualTo(user.getId());
    }
}