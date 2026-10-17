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
    public ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest updateItemRequest) {
        return null;
    }

    @Override
    public ItemDto getItemByID(Long userId, Long itemId) {
        return null;
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        return List.of();
    }

    @Override
    public List<ItemDto> getAllItemsBySearch(Long userId, String text) {
        return List.of();
    }
}
