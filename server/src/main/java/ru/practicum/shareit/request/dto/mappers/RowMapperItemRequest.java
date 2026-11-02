package ru.practicum.shareit.request.dto.mappers;

import ru.practicum.shareit.item.dto.mappers.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class RowMapperItemRequest {

    public static ItemRequest mapToItemRequestFromNewItemRequest(NewItemRequest newItemRequestRequest, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(newItemRequestRequest.getDescription());
        itemRequest.setRequestor(user);
        return itemRequest;
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }

        public static ItemRequestDto mapToItemRequestDtoWithItemList(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());

            if (itemRequest.getItems() != null) {
                List<ItemShortDto> itemsDto = itemRequest.getItems().stream()
                        .map(item -> {
                            ItemShortDto itemDto = new ItemShortDto();
                            itemDto.setId(item.getId());
                            itemDto.setName(item.getName());
                            itemDto.setOwnerId(item.getOwner().getId());
                            return itemDto;
                        })
                        .toList();
                itemRequestDto.setItems(itemsDto);
            } else {
                itemRequestDto.setItems(List.of());
            }
        return itemRequestDto;
    }

}
