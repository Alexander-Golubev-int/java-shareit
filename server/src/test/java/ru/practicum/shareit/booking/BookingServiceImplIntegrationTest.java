package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemStatus;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

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
        item.setDescription("Мощная");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @Test
    void createRequests_ShouldCreate() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto result = bookingService.createRequests(booker.getId(), request);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void createRequests_WhenItemNotAvailable_ShouldThrow() {
        item.setAvailable(false);
        itemRepository.save(item);

        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        assertThatThrownBy(() -> bookingService.createRequests(booker.getId(), request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void createRequests_WhenStartEqualsEnd_ShouldThrow() {
        LocalDateTime now = LocalDateTime.now().plusDays(1);
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(item.getId());
        request.setStart(now);
        request.setEnd(now);

        assertThatThrownBy(() -> bookingService.createRequests(booker.getId(), request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void createRequests_WhenEndBeforeStart_ShouldThrow() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(3));
        request.setEnd(LocalDateTime.now().plusDays(1));

        assertThatThrownBy(() -> bookingService.createRequests(booker.getId(), request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void createRequests_WhenStartInPast_ShouldThrow() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().minusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(1));

        assertThatThrownBy(() -> bookingService.createRequests(booker.getId(), request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void updateRequests_ShouldApprove() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto created = bookingService.createRequests(booker.getId(), request);

        BookingDto updated = bookingService.updateRequests(owner.getId(), created.getId(), true);

        assertThat(updated.getStatus()).isEqualTo(ItemStatus.APPROVED);
    }

    @Test
    void updateRequests_WhenNotOwner_ShouldThrow() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto created = bookingService.createRequests(booker.getId(), request);

        assertThatThrownBy(() -> bookingService.updateRequests(booker.getId(), created.getId(), true))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getRequest_ShouldReturnForBooker() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto created = bookingService.createRequests(booker.getId(), request);

        BookingDto found = bookingService.getRequest(booker.getId(), created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
    }

    @Test
    void getRequest_WhenStranger_ShouldThrow() {
        User stranger = new User();
        stranger.setName("Stranger");
        stranger.setEmail("s@mail.ru");
        stranger = userRepository.save(stranger);
        final User stranger2 = userRepository.getReferenceById(stranger.getId());

        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto created = bookingService.createRequests(booker.getId(), request);

        assertThatThrownBy(() -> bookingService.getRequest(stranger2.getId(), created.getId()))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void getAllRequest_ShouldReturnList() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.createRequests(booker.getId(), request);

        List<BookingDto> result = bookingService.getAllRequest(booker.getId(), BookingState.ALL);

        assertThat(result).isNotEmpty();
    }

    @Test
    void getAllOwnerRequest_ShouldReturnList() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.createRequests(booker.getId(), request);

        List<BookingDto> result = bookingService.getAllOwnerRequest(owner.getId(), "ALL");

        assertThat(result).isNotEmpty();
    }

    @Test
    void getAllOwnerRequest_WhenNoItems_ShouldReturnEmpty() {
        User emptyOwner = new User();
        emptyOwner.setName("Empty");
        emptyOwner.setEmail("empty@mail.ru");
        emptyOwner = userRepository.save(emptyOwner);

        List<BookingDto> result = bookingService.getAllOwnerRequest(emptyOwner.getId(), "ALL");

        assertThat(result).isEmpty();
    }
}