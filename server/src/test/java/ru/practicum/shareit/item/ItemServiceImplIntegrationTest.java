package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDtoDate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemStatus;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
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
    private CommentRepository commentRepository;

    private User owner;
    private User otherUser;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.ru");
        owner = userRepository.save(owner);

        otherUser = new User();
        otherUser.setName("OtherUser");
        otherUser.setEmail("otherUser@mail.ru");
        otherUser = userRepository.save(otherUser);

        item = new Item();
        item.setName("Макбук");
        item.setDescription("Якобы для программистов самое то");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @Test
    void getItemById_WhenUserIsOwner_ShouldReturnItemWithBookings() {
        Booking pastBooking = new Booking();
        pastBooking.setItem(item);
        pastBooking.setBooker(otherUser);
        pastBooking.setStartDate(LocalDateTime.now().minusDays(2));
        pastBooking.setEndDate(LocalDateTime.now().minusDays(1));
        pastBooking.setStatus(ItemStatus.APPROVED);
        bookingRepository.save(pastBooking);

        ItemDtoDate result = itemService.getItemByID(owner.getId(), item.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getNextBooking()).isNull();
    }

    @Test
    void getItemById_WhenUserIsNotOwner_ShouldReturnItemWithoutBookings() {
        Booking pastBooking = new Booking();
        pastBooking.setItem(item);
        pastBooking.setBooker(owner);
        pastBooking.setStartDate(LocalDateTime.now().minusDays(2));
        pastBooking.setEndDate(LocalDateTime.now().minusDays(1));
        pastBooking.setStatus(ItemStatus.APPROVED);
        bookingRepository.save(pastBooking);

        ItemDtoDate result = itemService.getItemByID(otherUser.getId(), item.getId());

        assertThat(result).isNotNull();
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
    }

    @Test
    void getItemById_ShouldReturnCommentsForAnyUser() {
        Comment comment = new Comment();
        comment.setText("Спасибо за мак винда привычнее");
        comment.setItem(item);
        comment.setUser(otherUser);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        ItemDtoDate resultOwner = itemService.getItemByID(owner.getId(), item.getId());

        ItemDtoDate resultOther = itemService.getItemByID(otherUser.getId(), item.getId());

        assertThat(resultOwner.getComments()).hasSize(1);
        assertThat(resultOther.getComments()).hasSize(1);
        assertThat(resultOther.getComments().get(0).getText()).isEqualTo("Спасибо за мак винда привычнее");
    }

}

