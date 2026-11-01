package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(Long userId, NewItemRequest newItemRequest);
    List<ItemRequestDto> getRequests(Long userId);
    List<ItemRequestDto> getAllRequests();
    ItemRequestDto getItemRequestById(Long requestId);
}
