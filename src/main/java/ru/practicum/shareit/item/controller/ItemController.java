package ru.practicum.shareit.item.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") @Validated @Positive(message = "ID должен быть больше 0") Long userId,
                              @Validated @RequestBody NewItemRequest itemRequest) {
        return itemService.createItem(userId, itemRequest);
    }

    @PatchMapping(path = "/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") @Validated @Positive(message = "ID должен быть " +
                                      "больше 0") Long userId, @PathVariable Long itemId,
                              @Validated @RequestBody UpdateItemRequest updateItemRequest) {
        return itemService.updateItem(userId, itemId, updateItemRequest);
    }
}

