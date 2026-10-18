package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImp implements ItemService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, NewItemRequest itemRequest) {
        log.info("Поиск пользователя с id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не существует"));
        log.info("Попытка создания новой вещи {} пользователем с id: {}", itemRequest.getName(),userId);
        Item item = RowMappersItem.mapToItemFromNewItemRequest(itemRequest, user);
        itemRepository.save(item);
        log.info("Вещь успешно добавлена {},", item);
        return RowMappersItem.mapToItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest updateItemRequest) {
        log.info("Попытка обновить информацию о вещи c id {} пользователем с userId: {}",
                itemId, userId);
        User user = userService.getUser(userId);
        Item oldItem = getItem(itemId);
        log.info("Проверка, что редактируемая вещь принадлежит владельцу вещи");
        if (!userId.equals(oldItem.getOwner().getId())) {
            log.warn("Ошибка. Попытка обновить вещь не принадлежащую владельцу вещи");
            throw new NotFoundException("Вещь с id " + itemId + " не принадлежит отправителю запроса");
        }
        RowMappersItem.updateItemFields(oldItem, updateItemRequest);
        return RowMappersItem.mapToItemDto(itemRepository.save(oldItem));
    }

    @Override
    public ItemDto getItemByID(Long userId, Long itemId) {
        log.info("Поиск вещи с id: {}", itemId);
        return RowMappersItem.mapToItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + itemId + " не найдена")));
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        log.info("Попытка запросить информацию о всех вещах пользователя с userId: {}", userId);
        userService.getUserById(userId);
        return itemRepository.findByOwner_Id(userId).stream().map(RowMappersItem::mapToItemDto).toList();
    }

    @Override
    public List<ItemDto> getAllItemsBySearch(Long userId, String text) {
        userService.getUserById(userId);
        log.info("Поиск вещей по названию и описанию используя текст = {}", text);
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.searchAvailableItems(text).stream().map(RowMappersItem::mapToItemDto).toList();
    }

    @Override
    public Item getItem(Long itemId) {
        log.info("Поиск вещи с id: {}", itemId);
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + itemId + " не найдена"));
    }
}
