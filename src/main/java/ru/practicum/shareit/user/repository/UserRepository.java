package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.mappers.RowMappersUser;
import ru.practicum.shareit.user.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbc;
    private final RowMappersUser rowMapperUser;

    private static final String insertNewUser = "INSERT INTO users(name, email) VALUES (?, ?)";
    private static final String findByIdQuery = "SELECT * FROM users WHERE id = ?";
    private static final String updateUser = "UPDATE users SET name = ?, email = ? WHERE id = ?";
    private static final String deleteUser = "DELETE FROM users WHERE id = ?";

    public User createNewUser(NewUserRequest newUserRequest) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertNewUser, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, newUserRequest.getName());
                ps.setString(2, newUserRequest.getEmail());
                return ps;
            }, keyHolder);
            log.info("Пользователь с id: {} успешно создан", keyHolder.getKey());
        } catch (DuplicateKeyException e) {
            log.error("Попытка создания пользователя с почтой которая уже есть в бд: {}", newUserRequest.getEmail());
            throw new ValidationException("Пользователь с email: " + newUserRequest.getEmail() + " уже существует");
        }
        Long userId = keyHolder.getKeyAs(Long.class);
        return getUserById(userId);
    }

    public User getUserById(Long id) {
        try {
            log.info("Поиск user с id {}", id);
            return jdbc.queryForObject(findByIdQuery, rowMapperUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id " + id + " не существует");
        }
    }

    public User updateUserFields(User userToUpdate) {
        log.info("Обновление полей у user с id: {} на новые поля name: {}, email: {}", userToUpdate.getId(),
                userToUpdate.getName(), userToUpdate.getEmail());
        try {
            jdbc.update(updateUser, userToUpdate.getName(), userToUpdate.getEmail(), userToUpdate.getId());
            return getUserById(userToUpdate.getId());
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("Пользователь с таким email уже существует, заменить email невозможно");
        }
    }

    public void deleteUserById(Long id) {
        jdbc.update(deleteUser, id);
        log.info("Пользователь с id {} был успешно удален", id);
    }
}
