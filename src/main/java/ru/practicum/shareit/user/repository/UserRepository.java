package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
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

    private static final String INSERT_NEW_USER = "INSERT INTO users(name, email) VALUES (?, ?)";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";

    public User createNewUser(NewUserRequest newUserRequest) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_NEW_USER, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, newUserRequest.getName());
                ps.setString(2, newUserRequest.getEmail());
                return ps;
            }, keyHolder);
            log.info("Пользователь с id: {} успешно создан", keyHolder.getKey());
        } catch (DuplicateKeyException e) {
            log.info("Попытка создания пользователя с почтой которая уже есть в бд: {}", newUserRequest.getEmail());
            throw new ValidationException("Пользователь с email: " + newUserRequest.getEmail() + " уже существует");
        }
        Long userId = keyHolder.getKeyAs(Long.class);
        return jdbc.queryForObject(FIND_BY_ID_QUERY, rowMapperUser, userId);
    }
}
