package ru.practicum.shareit.request.dto.mappers;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.mappers.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RowMapperItemRequestTest {

    @Test
    void mapToItemRequestFromNewItemRequest_ShouldMapCorrectly() {
        NewItemRequest request = new NewItemRequest();
        request.setDescription("Нужна дрель");

        User user = new User();
        user.setId(1L);
        user.setName("Ivan");
        user.setEmail("ivan@mail.ru");

        ItemRequest result = RowMapperItemRequest.mapToItemRequestFromNewItemRequest(request, user);

        assertThat(result.getDescription()).isEqualTo("Нужна дрель");
        assertThat(result.getRequestor()).isEqualTo(user);
        assertThat(result.getId()).isNull();
    }

    @Test
    void mapToItemRequestDto_ShouldMapCorrectly() {
        User user = new User();
        user.setId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(10L);
        itemRequest.setDescription("Описание запроса");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.of(2024, 1, 15, 12, 0));

        ItemRequestDto dto = RowMapperItemRequest.mapToItemRequestDto(itemRequest);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getDescription()).isEqualTo("Описание запроса");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 15, 12, 0));
        assertThat(dto.getItems()).isNull();
    }

    @Test
    void mapToItemRequestDtoWithItemList_WhenItemsNotNull_ShouldMapItems() {
        User owner = new User();
        owner.setId(7L);
        owner.setName("Owner");

        Item item = new Item();
        item.setId(5L);
        item.setName("Дрель");
        item.setOwner(owner);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(10L);
        itemRequest.setDescription("Нужна дрель");
        itemRequest.setCreated(LocalDateTime.of(2024, 2, 1, 10, 0));
        itemRequest.setItems(List.of(item));

        ItemRequestDto dto = RowMapperItemRequest.mapToItemRequestDtoWithItemList(itemRequest);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2024, 2, 1, 10, 0));
        assertThat(dto.getItems()).hasSize(1);

        ItemShortDto shortDto = dto.getItems().get(0);
        assertThat(shortDto.getId()).isEqualTo(5L);
        assertThat(shortDto.getName()).isEqualTo("Дрель");
        assertThat(shortDto.getOwnerId()).isEqualTo(7L);
    }

    @Test
    void mapToItemRequestDtoWithItemList_WhenItemsNull_ShouldReturnEmptyList() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(11L);
        itemRequest.setDescription("Без вещей");
        itemRequest.setCreated(LocalDateTime.of(2024, 3, 1, 9, 0));
        itemRequest.setItems(null);

        ItemRequestDto dto = RowMapperItemRequest.mapToItemRequestDtoWithItemList(itemRequest);

        assertThat(dto.getId()).isEqualTo(11L);
        assertThat(dto.getDescription()).isEqualTo("Без вещей");
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void mapToItemRequestDtoWithItemList_WhenItemsEmpty_ShouldReturnEmptyList() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(12L);
        itemRequest.setDescription("Пустой список");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setItems(List.of());

        ItemRequestDto dto = RowMapperItemRequest.mapToItemRequestDtoWithItemList(itemRequest);

        assertThat(dto.getItems()).isEmpty();
    }
}