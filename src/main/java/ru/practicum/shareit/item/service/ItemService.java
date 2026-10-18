package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, NewItemRequest itemRequest);
    ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest updateItemRequest);
    ItemDto getItemByID(Long userId, Long itemId);
    List<ItemDto> getAllItems(Long userId);
    List<ItemDto> getAllItemsBySearch(Long userId, String text);
    Item getItem(Long itemId);
}
