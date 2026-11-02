package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private NewUserRequest newUserRequest;

    @BeforeEach
    void setUp() {
        newUserRequest = new NewUserRequest();
        newUserRequest.setName("Test User");
        newUserRequest.setEmail("test@mail.ru");
    }

    @Test
    void createUser_ShouldSaveUserToDatabase() {
        UserDto result = userService.createUser(newUserRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("test@mail.ru");

        User savedUser = userRepository.findById(result.getId()).orElseThrow();
        assertThat(savedUser.getName()).isEqualTo("Test User");
        assertThat(savedUser.getEmail()).isEqualTo("test@mail.ru");
    }

    @Test
    void createUser_WhenEmailExists_ShouldThrowException() {
        User existingUser = new User();
        existingUser.setName("Существующий пользак");
        existingUser.setEmail("test@mail.ru");
        userRepository.save(existingUser);

        assertThatThrownBy(() -> userService.createUser(newUserRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Пользователь с таким email уже существует");
    }
}