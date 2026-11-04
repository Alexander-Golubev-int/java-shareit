package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated @RequestBody UserRequestDto userRequest) {
        return userClient.createUser(userRequest);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable @Positive(message = "id должен быть больше 0") Long id, @Valid @RequestBody UpdateUserRequest userRequest) {
        return userClient.updateUser(id, userRequest);
    }


    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable @Validated @Positive(message = "id должен быть больше 0") Long id) {
        return userClient.getUser(id);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable @Positive(message = "id должен быть больше 0") Long id) {
        return userClient.deleteUser(id);
    }

}
