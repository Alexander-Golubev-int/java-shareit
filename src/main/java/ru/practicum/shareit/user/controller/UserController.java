package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@Valid
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Validated @RequestBody NewUserRequest userRequest) {
        return userService.createUser(userRequest);
    }
}
