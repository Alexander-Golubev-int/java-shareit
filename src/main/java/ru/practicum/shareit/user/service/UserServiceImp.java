package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mappers.RowMappersUser;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto getUserById(Long id) {
        log.info("Поиск пользователя с id: {}", id);
        return RowMappersUser.mapToUserDtoWithId(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не существует")));
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest userRequest) {
        log.info("Попытка создания пользователя");

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new ValidationException("Пользователь с таким email уже существует");
        }

        User user = RowMappersUser.mapToNewUser(userRequest);
        User savedUser = userRepository.save(user);

        log.info( "Пользователь с id: {} успешно создан", savedUser.getId());
        return RowMappersUser.mapToUserDtoWithId(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UpdateUserRequest userRequest) {
        log.info("Поиск и получения пользователя с id: {}", id);
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не существует"));

        if (userRepository.existsByEmailAndIdNot(userRequest.getEmail(), userToUpdate.getId())) {
            throw new ValidationException("Email уже занят другим пользователем");
        }

        RowMappersUser.updateUserFields(userToUpdate, userRequest);

        log.info("Попытка обновления пользователя с id {}", id);
        userToUpdate = userRepository.save(userToUpdate);
        return RowMappersUser.mapToUserDtoWithId(userToUpdate);
    }

    @Override
    public Map<String, String> deleteUserById(Long id) {
        getUserById(id);
        log.info("Попытка удаления пользователя с id: {}", id);
        userRepository.deleteById(id);
        return Map.of("message", "пользователь успешно удален");
    }
}
