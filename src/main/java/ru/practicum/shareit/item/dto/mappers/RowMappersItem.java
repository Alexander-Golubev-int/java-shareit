package ru.practicum.shareit.item.dto.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDate;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Component
public class RowMappersItem {

    public static Item mapToItemFromNewItemRequest(NewItemRequest newItemRequest, User userId) {
        Item item = new Item();
        item.setName(newItemRequest.getName());
        item.setDescription(newItemRequest.getDescription());
        item.setAvailable(newItemRequest.getAvailable());
        item.setOwner(userId);
        return item;
    }

    public static ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        return dto;
    }

    public static ItemDtoDate mapToItemDtoData(Item item, Optional<Booking> lastBooking,
                                               Optional<Booking> nearBooking, List<Comment> comments) {
        ItemDtoDate dto = new ItemDtoDate();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        lastBooking.ifPresent(booking -> dto.setLastBooking(booking.getEndDate()));
        nearBooking.ifPresent(booking -> dto.setNextBooking(booking.getStartDate()));
        dto.setComments(comments);
        return dto;
    }

    public static void updateItemFields(Item item, UpdateItemRequest request) {
        if (request.getName() != null) {
            item.setName(request.getName());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getAvailable() != null) {
            item.setAvailable(request.getAvailable());
        }
    }
}