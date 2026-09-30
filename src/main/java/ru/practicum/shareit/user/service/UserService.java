package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mappers.RowMappersUser;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto getUserById(Long id) {
        return RowMappersUser.mapToUserDtoWithId(userRepository.getUserById(id));
    }

    public Map<String, String> deleteUserById(Long id) {
        getUserById(id);
        log.info("Попытка удаления пользователя с id: {}", id);
        userRepository.deleteUserById(id);
        return Map.of("message", "пользователь успешно удален");
    }

    public UserDto createUser(NewUserRequest userRequest) {
        log.info("Попытка создания пользователя с именемм {} и почтой {}", userRequest.getName(), userRequest.getEmail());
        return RowMappersUser.mapToUserDtoWithId(userRepository.createNewUser(userRequest));
    }

    public UserDto updateUser(Long id, UpdateUserRequest userRequest) {
        log.info("Попытка обновления пользователя с id {}", id);
        User userToUpdate = RowMappersUser.updateUserFields(userRepository.getUserById(id), userRequest);
        return RowMappersUser.mapToUserDtoWithId(userRepository.updateUserFields(userToUpdate));
    }

}
