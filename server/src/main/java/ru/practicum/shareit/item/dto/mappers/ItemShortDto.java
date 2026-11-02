package ru.practicum.shareit.item.dto.mappers;

import lombok.Data;

@Data
public class ItemShortDto {
    private Long id;
    private String name;
    private Long ownerId;
}
