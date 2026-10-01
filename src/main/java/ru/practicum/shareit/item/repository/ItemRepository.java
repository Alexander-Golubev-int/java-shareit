package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.mappers.RowMappersItem;
import ru.practicum.shareit.item.model.Item;

import java.sql.PreparedStatement;
import java.sql.Statement;


@Slf4j
@RequiredArgsConstructor
@Repository
public class ItemRepository {
    private final JdbcTemplate jdbc;
    private final RowMappersItem rowMappersItem;

    private final String INSERT_NEW_ITEM = "INSERT INTO items(name, description, available, owner_id) VALUES (?, ?, " +
            "?, ?)";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM items WHERE id = ?";
    private static final String UPDATE_ITEM = "UPDATE items SET name = ?, description = ?, available = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";


    public Item createItem(Long userId, NewItemRequest newItemRequest) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_NEW_ITEM, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, newItemRequest.getName());
                ps.setString(2, newItemRequest.getDescription());
                ps.setBoolean(3, newItemRequest.getAvailable());
                ps.setLong(4, userId);
                return ps;
            }, keyHolder);
            log.info("Вещь с id: {} успешно создана", keyHolder.getKey());
        } catch (DataAccessException e) {
            log.info("При добавлении новой вещи в бд произошла ошибка. Трасса: {}", e.getMessage());
            throw new ValidationException("При добавлении вещи произошла ошибка. Попробуйте позже или измените тело " +
                    "запроса.");
        }
        Long itemId = keyHolder.getKeyAs(Long.class);
        return getItemById(itemId);
    }

    public Item updateItemFields(Item itemToUpdate) {
        log.info("Обновление полей у item c id {}", itemToUpdate.getId());
        try {
            jdbc.update(UPDATE_ITEM, itemToUpdate.getName(), itemToUpdate.getDescription(),
                    itemToUpdate.getAvailable(), itemToUpdate.getId());
            return getItemById(itemToUpdate.getId());
        } catch (DataAccessException e) {
            log.info("При обновлении вещи в бд произошла ошибка. Трасса: {}", e.getMessage());
            throw new ValidationException("При обновлении информации о вещи произошла ошибка. Попробуйте позже или " +
                    "измените тело запроса.");
        }
    }

    public Item getItemById(Long id) {
        try {
            log.info("Поиск вещи с id {}", id);
            return jdbc.queryForObject(FIND_BY_ID_QUERY, rowMappersItem, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Вещь с id " + id + " не существует");
        }
    }
}
