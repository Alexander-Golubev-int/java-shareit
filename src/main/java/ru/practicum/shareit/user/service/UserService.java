package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Map;

public interface UserService {
    UserDto getUserById(Long id);
    UserDto createUser(NewUserRequest newUserRequest);
    UserDto updateUser(Long id, UpdateUserRequest userRequest);
    Map<String, String> deleteUserById(Long id);
    User getUser(Long id);
}
