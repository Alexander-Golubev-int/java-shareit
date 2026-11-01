package ru.practicum.shareit.request.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constans.Constants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID должен быть " +
                                       "больше 0") Long userId,
                                        @Validated @RequestBody NewItemRequest newItemRequest) {
        return itemRequestService.createRequest(userId, newItemRequest);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequest(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID должен быть " +
            "больше 0") Long userId) {
        return itemRequestService.getRequests(userId);
    }

    @GetMapping(path = "/all")
    public List<ItemRequestDto> getAllItemRequest() {
        return itemRequestService.getAllRequests();
    }

    @GetMapping(path = "/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(requestId);
    }
}
