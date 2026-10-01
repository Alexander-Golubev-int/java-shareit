package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.dto.mappers.RowMappersItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;

    public ItemDto createItem(Long userId, NewItemRequest itemRequest) {
        log.info("Попытка создания новой вещи {} пользователем с id: {}", itemRequest.getName(),userId);
        userService.getUserById(userId);
        return RowMappersItem.mapToItemDto(itemRepository.createItem(userId, itemRequest));
    }

    public ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest updateItemRequest) {
        log.info("Попытка обновить информацию о вещи c id {} пользователем с user_id: {}",
                itemId, userId);
        userService.getUserById(userId);
        Item oldItem = itemRepository.getItemById(itemId);
        log.info("Проверка, что редактируемая вещь принадлежит владельцу вещи");
        if (!userId.equals(oldItem.getUserId())) {
            log.error("Ошибка. Попытка обновить вещь не принадлежащую владельцу вещи");
            throw new NotFoundException("Вещь с id " + itemId + " не найдена");
        }
        RowMappersItem.updateItemFields(oldItem, updateItemRequest);
        return RowMappersItem.mapToItemDto(itemRepository.updateItemFields(oldItem));
    }
}
