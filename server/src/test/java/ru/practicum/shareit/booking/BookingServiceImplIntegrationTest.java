package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

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
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Макбук");
        item.setDescription("Якобы для программистов самое то");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @Test
    void createRequests_ShouldSaveBookingToDatabase() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto result = bookingService.createRequests(booker.getId(), request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStart()).isEqualTo(request.getStart());
        assertThat(result.getEnd()).isEqualTo(request.getEnd());

        Booking savedBooking = bookingRepository.findById(result.getId()).orElseThrow();
        assertThat(savedBooking.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(savedBooking.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void createRequests_WhenItemIsNotAvailable_ShouldThrowException() {
        item.setAvailable(false);
        itemRepository.save(item);

        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        assertThatThrownBy(() -> bookingService.createRequests(booker.getId(), request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("не доступна");
    }
}