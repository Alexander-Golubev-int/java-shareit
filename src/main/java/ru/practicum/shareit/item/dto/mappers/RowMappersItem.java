package ru.practicum.shareit.item.dto.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RowMappersItem implements RowMapper<Item> {
    @Override
    public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
        Item item = new Item();
//        item.setId(rs.getLong("id"));
//        item.setName(rs.getString("name"));
//        item.setDescription(rs.getString("description"));
//        item.setAvailable(rs.getBoolean("available"));
//        item.setUserId(rs.getLong("owner_id"));
//        item.setRequestId(rs.getObject("request_id", Long.class));
      return item;
    }

    public static Item mapToItemFromNewItemRequest(NewItemRequest newItemRequest, User userId) {
        Item item = new Item();
        item.setName(newItemRequest.getName());
        item.setDescription(newItemRequest.getDescription());
        item.setAvailable(newItemRequest.getAvailable());
        item.setUserId(userId);
        return item;
    }

    public static ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        return dto;
    }

    public static Item updateItemFields(Item item, UpdateItemRequest request) {
        if (request.getName() != null) {
            item.setName(request.getName());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getAvailable() != null) {
            item.setAvailable(request.getAvailable());
        }
        return item;
    }
}