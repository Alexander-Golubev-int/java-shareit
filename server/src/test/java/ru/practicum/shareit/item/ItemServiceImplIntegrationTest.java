package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemStatus;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.ru");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@mail.ru");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Дрель");
        item.setDescription("Мощная дрель");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @Test
    void createItem_WithoutRequestId_ShouldCreate() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Отвёртка");
        request.setDescription("Крестовая");
        request.setAvailable(true);

        ItemDto result = itemService.createItem(owner.getId(), request);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Отвёртка");
    }

    @Test
    void createItem_WithExistingRequestId_ShouldLinkRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Нужна дрель");
        itemRequest.setRequestor(booker);
        itemRequest = itemRequestRepository.save(itemRequest);

        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель2");
        request.setDescription("Ещё одна");
        request.setAvailable(true);
        request.setRequestId(itemRequest.getId());

        ItemDto result = itemService.createItem(owner.getId(), request);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Дрель2");
    }

    @Test
    void createItem_WithNonExistingRequestId_ShouldStillCreate() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель3");
        request.setDescription("Без запроса");
        request.setAvailable(true);
        request.setRequestId(999L);

        ItemDto result = itemService.createItem(owner.getId(), request);

        assertThat(result.getId()).isNotNull();
    }

    @Test
    void createItem_WhenUserNotFound_ShouldThrow() {
        NewItemRequest request = new NewItemRequest();
        request.setName("X");
        request.setDescription("Y");
        request.setAvailable(true);

        assertThatThrownBy(() -> itemService.createItem(999L, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateItem_ShouldUpdate() {
        UpdateItemRequest update = new UpdateItemRequest();
        update.setName("Новая дрель");
        update.setDescription("Очень мощная");
        update.setAvailable(false);

        ItemDto result = itemService.updateItem(owner.getId(), item.getId(), update);

        assertThat(result.getName()).isEqualTo("Новая дрель");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    void updateItem_WhenNotOwner_ShouldThrow() {
        UpdateItemRequest update = new UpdateItemRequest();
        update.setName("Хак");

        assertThatThrownBy(() -> itemService.updateItem(booker.getId(), item.getId(), update))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getItemByID_WhenOwner_ShouldReturnWithBookings() {
        Booking past = new Booking();
        past.setItem(item);
        past.setBooker(booker);
        past.setStartDate(LocalDateTime.now().minusDays(5));
        past.setEndDate(LocalDateTime.now().minusDays(3));
        past.setStatus(ItemStatus.APPROVED);
        bookingRepository.save(past);

        ItemDtoDate result = itemService.getItemByID(owner.getId(), item.getId());

        assertThat(result.getId()).isEqualTo(item.getId());
    }

    @Test
    void getItemByID_WhenNotOwner_ShouldReturnWithoutBookings() {
        ItemDtoDate result = itemService.getItemByID(booker.getId(), item.getId());

        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
    }

    @Test
    void getAllItems_ShouldReturnOwnerItems() {
        List<ItemDto> result = itemService.getAllItems(owner.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void getAllItemsBySearch_ShouldFind() {
        List<ItemDto> result = itemService.getAllItemsBySearch(owner.getId(), "дрель");

        assertThat(result).isNotEmpty();
    }

    @Test
    void getAllItemsBySearch_WhenBlank_ShouldReturnEmpty() {
        List<ItemDto> result = itemService.getAllItemsBySearch(owner.getId(), "   ");

        assertThat(result).isEmpty();
    }

    @Test
    void createComment_WhenHadBooking_ShouldCreate() {
        Booking past = new Booking();
        past.setItem(item);
        past.setBooker(booker);
        past.setStartDate(LocalDateTime.now().minusDays(10));
        past.setEndDate(LocalDateTime.now().minusDays(5));
        past.setStatus(ItemStatus.APPROVED);
        bookingRepository.save(past);

        NewCommentRequest commentReq = new NewCommentRequest();
        commentReq.setText("Отличная дрель!");

        CommentDto result = itemService.createComment(booker.getId(), item.getId(), commentReq);

        assertThat(result.getText()).isEqualTo("Отличная дрель!");
        assertThat(result.getAuthorName()).isEqualTo("Booker");
    }

    @Test
    void createComment_WhenNoPastBooking_ShouldThrow() {
        NewCommentRequest commentReq = new NewCommentRequest();
        commentReq.setText("Попытка");

        assertThatThrownBy(() -> itemService.createComment(booker.getId(), item.getId(), commentReq))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void getItem_WhenNotFound_ShouldThrow() {
        assertThatThrownBy(() -> itemService.getItem(999L))
                .isInstanceOf(NotFoundException.class);
    }
}