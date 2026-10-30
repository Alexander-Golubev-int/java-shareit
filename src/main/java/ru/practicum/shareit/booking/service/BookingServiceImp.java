package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.mappers.RowMapperBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingDto createRequests(Long userId, NewBookingRequest newBookingRequest) {
        checkDate(newBookingRequest.getStart(), newBookingRequest.getEnd());
        Item item = itemService.getItem(newBookingRequest.getItemId());
        User user = userService.getUser(userId);
        checkAvailable(item);
        Booking booking = RowMapperBooking.mapToBookingFromNewBookingRequest(newBookingRequest, item, user);
        log.info("Попытка создания запроса на бронирование вещи");
        booking = bookingRepository.save(booking);
        return RowMapperBooking.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto updateRequests(Long userId, Long bookingId, Boolean approved) {
        userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Пользователь не владеет вещью аренды"));
        log.info("Поиск бронирования с id: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование " + bookingId + " не найдено"));
        Item item = itemService.getItem(booking.getItem().getId());
        if (!item.getOwner().getId().equals(userId)) {
            log.warn("Ошибка. Попытка обновить резервацию вещи не принадлежащую отправителю запроса");
            throw new NotFoundException("Вещь с id " + item.getOwner().getId() + " не принадлежит отправителю запроса");
        }
        log.info("Обновление состояние статуса с {} на {}", booking.getStatus(), approved);
        RowMapperBooking.updateBookingStatus(booking, approved);
        booking = bookingRepository.save(booking);
        return RowMapperBooking.mapToBookingDto(booking);
    }

    @Override
    public BookingDto getRequest(Long userId, Long bookingId) {
        log.info("Запрос на получение данных о бронирование пользователем {} к id бронирования {}", userId, bookingId);
        User user = userService.getUser(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование " + bookingId + " не найдено"));
        Item item = itemService.getItem(booking.getItem().getId());
        if (!(booking.getBooker().getId().equals(user.getId()) || item.getOwner().getId().equals(user.getId()))) {
            throw new BadRequestException("Пользователь запроса не владеет вещью аренды");
        }
        return RowMapperBooking.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllRequest(Long userId, BookingState state) {
        log.info("Запрос на получение всех данных о бронирование пользователем {}", userId);
        User user = userService.getUser(userId);
        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository.findCurrentBookingsByBooker(user.getId());
            case PAST -> bookingRepository.findPastBookingsByBooker(user.getId());
            case FUTURE -> bookingRepository.findFutureBookingsByBooker(user.getId());
            case WAITING -> bookingRepository.findWaitingBookingsByBooker(user.getId());
            case REJECTED -> bookingRepository.findRejectedBookingsByBooker(user.getId());
            case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(user.getId());
        };

        return bookings.stream()
                .map(RowMapperBooking::mapToBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getAllOwnerRequest(Long userId, String state) {
        log.info("Запрос на получение всех броней владельцем вещей {}", userId);
        User user = userService.getUser(userId);
        List<Item> itemList = itemRepository.findAllByOwnerId(user.getId());
        if (itemList.isEmpty()) {
            return List.of();
        }
        return bookingRepository.findAllByOwnerId(user.getId())
                .stream().map(RowMapperBooking::mapToBookingDto)
                .toList();
    }


    private void checkDate(LocalDateTime start, LocalDateTime end) {
        log.info("Запуск проверки валидации даты начала {} и даты конца {}", start, end);
        if (start.isEqual(end)) {
            throw new BadRequestException("Дата начала аренды не должна быть равна дате конца аренды");
        }
        if (end.isBefore(start)) {
            throw new BadRequestException("Дата конца аренды не должна быть раньше даты начала аренды");
        }
        if (end.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата конца аренды не может быть в прошлом");
        }
        if (end.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата конца аренды не может быть в прошлом");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата начала аренды не может быть в прошлом");
        }
    }

    private void checkAvailable(Item item) {
        log.info("Проверка, что у item статус доступа available = true");
        if (!item.getAvailable()) {
            throw new BadRequestException("В данный момент вещь не доступна, обратитесь позже");
        }
    }
}
