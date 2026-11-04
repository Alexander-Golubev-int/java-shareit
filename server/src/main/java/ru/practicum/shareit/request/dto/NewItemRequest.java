package ru.practicum.shareit.request.dto;

import lombok.Data;

@Data
public class NewItemRequest {
    private Long userId;
    private String description;
}
