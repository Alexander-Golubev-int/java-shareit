package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Map;

@Valid
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody NewUserRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @PatchMapping(path = "/{id}")
    public UserDto updateUser(@PathVariable @Positive Long id, @Valid @RequestBody UpdateUserRequest userRequest) {
        return userService.updateUser(id, userRequest);
    }

    @GetMapping(path = "/{id}")
    public UserDto getUserById(@PathVariable @Positive Long id) {
        return userService.getUserById(id);
    }

    @DeleteMapping(path = "/{id}")
    public Map<String, String> deleteUserById(@PathVariable @Positive Long id) {
        return userService.deleteUserById(id);
    }
}
