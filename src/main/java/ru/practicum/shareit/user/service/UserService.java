package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mappers.RowMappersUser;
import ru.practicum.shareit.user.repository.UserRepository;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto createUser(NewUserRequest userRequest) {
        log.info("Попытка создания пользователя с именемм {} и почтой {}", userRequest.getName(), userRequest.getEmail());
        return RowMappersUser.mapToUserDtoWithId(userRepository.createNewUser(userRequest));
    }
}
