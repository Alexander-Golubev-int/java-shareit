package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.validation.NotBlankIfPresent;

@Data
public class UpdateItemRequest {
    @NotBlankIfPresent
    private String name;
    @NotBlankIfPresent
    private String description;
    private Boolean available;
}
