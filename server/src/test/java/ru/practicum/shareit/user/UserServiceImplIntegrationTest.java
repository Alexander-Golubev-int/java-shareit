package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
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

    private User user;

    @BeforeEach
    void setUp() {
        newUserRequest = new NewUserRequest();
        newUserRequest.setName("Test User");
        newUserRequest.setEmail("test@mail.ru");

        user = new User();
        user.setName("Existing User");
        user.setEmail("existing@mail.ru");
        user = userRepository.save(user);
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
        User duplicateUser = new User();
        duplicateUser.setName("Duplicate");
        duplicateUser.setEmail("test@mail.ru");
        userRepository.save(duplicateUser);

        assertThatThrownBy(() -> userService.createUser(newUserRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Пользователь с таким email уже существует");
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        UserDto result = userService.getUserById(user.getId());
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getName()).isEqualTo("Existing User");
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldThrowException() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("не существует");
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setName("Updated Name");

        UserDto result = userService.updateUser(user.getId(), updateRequest);
        assertThat(result.getName()).isEqualTo("Updated Name");

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
    }

    @Test
    void deleteUserById_ShouldDeleteUser() {
        userService.deleteUserById(user.getId());
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }
}