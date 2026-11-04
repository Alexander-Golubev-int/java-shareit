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
import ru.practicum.shareit.user.service.UserService;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    private NewUserRequest newUserRequest;

    @BeforeEach
    void setUp() {
        newUserRequest = new NewUserRequest();
        newUserRequest.setName("Ivan");
        newUserRequest.setEmail("ivan@mail.ru");
    }

    @Test
    void createUser_ShouldCreateSuccessfully() {
        UserDto result = userService.createUser(newUserRequest);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Ivan");
        assertThat(result.getEmail()).isEqualTo("ivan@mail.ru");
    }

    @Test
    void createUser_WhenEmailExists_ShouldThrowValidationException() {
        userService.createUser(newUserRequest);

        NewUserRequest duplicate = new NewUserRequest();
        duplicate.setName("Petr");
        duplicate.setEmail("ivan@mail.ru");

        assertThatThrownBy(() -> userService.createUser(duplicate))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void getUserById_ShouldReturnUser() {
        UserDto created = userService.createUser(newUserRequest);

        UserDto found = userService.getUserById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getEmail()).isEqualTo("ivan@mail.ru");
    }

    @Test
    void getUserById_WhenNotExists_ShouldThrowNotFound() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateUser_ShouldUpdateFields() {
        UserDto created = userService.createUser(newUserRequest);

        UpdateUserRequest update = new UpdateUserRequest();
        update.setName("Ivan Updated");
        update.setEmail("ivan_new@mail.ru");

        UserDto updated = userService.updateUser(created.getId(), update);

        assertThat(updated.getName()).isEqualTo("Ivan Updated");
        assertThat(updated.getEmail()).isEqualTo("ivan_new@mail.ru");
    }

    @Test
    void updateUser_WhenEmailTakenByAnother_ShouldThrow() {
        UserDto user1 = userService.createUser(newUserRequest);

        NewUserRequest user2Req = new NewUserRequest();
        user2Req.setName("Petr");
        user2Req.setEmail("petr@mail.ru");
        UserDto user2 = userService.createUser(user2Req);

        UpdateUserRequest update = new UpdateUserRequest();
        update.setEmail("ivan@mail.ru");

        assertThatThrownBy(() -> userService.updateUser(user2.getId(), update))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void deleteUserById_ShouldDelete() {
        UserDto created = userService.createUser(newUserRequest);

        Map<String, String> result = userService.deleteUserById(created.getId());

        assertThat(result.get("message")).contains("успешно удален");
        assertThatThrownBy(() -> userService.getUserById(created.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getUser_ShouldReturnEntity() {
        UserDto created = userService.createUser(newUserRequest);

        var user = userService.getUser(created.getId());

        assertThat(user.getId()).isEqualTo(created.getId());
        assertThat(user.getEmail()).isEqualTo("ivan@mail.ru");
    }
}