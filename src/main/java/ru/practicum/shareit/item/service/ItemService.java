package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto createItem(Long userId, NewItemRequest itemRequest);

    ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest updateItemRequest);

    ItemDtoDate getItemByID(Long userId, Long itemId);

    List<ItemDto> getAllItems(Long userId);

    List<ItemDto> getAllItemsBySearch(Long userId, String text);

    Item getItem(Long itemId);

    CommentDto createComment(Long userId, Long itemId, NewCommentRequest request);
}
