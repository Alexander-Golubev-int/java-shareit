package ru.practicum.shareit.item.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

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

    @GetMapping(path = "/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") @Validated @Positive(message = "ID должен быть " +
            "больше 0") Long userId, @PathVariable Long itemId) {
        return itemService.getItemByID(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") @Validated @Positive(message = "ID " +
            "должен быть больше 0") Long userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping(path = "/search")
    public Collection<ItemDto> getAllItemsBySearch(@RequestHeader("X-Sharer-User-Id") @Validated @Positive(message =
            "ID должен быть больше 0") Long userId, @RequestParam String text) {
        return itemService.getAllItemsBySearch(userId, text);
    }
}

