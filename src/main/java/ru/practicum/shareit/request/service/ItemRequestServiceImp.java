package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.dto.mappers.RowMapperItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImp implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    @Override
    public ItemRequestDto createRequest(Long userId, NewItemRequest newItemRequest) {
        User user = userService.getUser(userId);
        ItemRequest itemRequest = RowMapperItemRequest.mapToItemRequestFromNewItemRequest(newItemRequest, user);
        log.info("Добавление нового запрос в базу данных {}", itemRequest);
        itemRequestRepository.save(itemRequest);
        itemRequest = itemRequestRepository.findById(itemRequest.getId()).orElseThrow();
        return RowMapperItemRequest.mapToItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getRequests(Long userId) {
        User user = userService.getUser(userId);
        log.info("Поиск всех запросов пользователя с id {}", user.getId());
        List<ItemRequest> itemRequestList = itemRequestRepository.findByRequestorIdWithItems(user.getId());
        if (itemRequestList.isEmpty()) {
            return List.of();
        }
        return itemRequestList.stream()
                .map(RowMapperItemRequest::mapToItemRequestDtoWithItemList)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllRequests() {
        log.info("Поиск всех запросов на поиск вещей");
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByOrderByCreatedDesc();
        if (itemRequestList.isEmpty()) {
            return List.of();
        }
        return itemRequestList.stream()
                .map(RowMapperItemRequest::mapToItemRequestDto)
                .toList();

    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId) {
        log.info("Поиск конкретного запроса {}", requestId);
        ItemRequest request = itemRequestRepository.findByIdWithItems(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));
        return RowMapperItemRequest.mapToItemRequestDtoWithItemList(request);
    }
}
