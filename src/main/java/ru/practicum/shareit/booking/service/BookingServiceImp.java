package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.mappers.RowMapperBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
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
    public BookingDto updateRequests(Long userId, Long bookingId, Boolean approved) {
        userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Пользователь не владеет вещью аренды"));
        log.info("Поиск бронирования с id: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование " +  bookingId + " не найдено"));
        Item item = itemService.getItem(booking.getItem().getId());
        if (!item.getOwner().getId().equals(userId)) {
            log.warn("Ошибка. Попытка обновить резервацию вещи не принадлежащую отправителю запроса");
            throw new NotFoundException("Вещь с id " + item.getOwner().getId() + " не принадлежит отправителю запроса");
        }
        log.info("Обновление состояние статуса с {} на {}", booking.getStatus(), approved);
        RowMapperBooking.updateBookingStatus(booking, approved);
        bookingRepository.save(booking);
        return RowMapperBooking.mapToBookingDto(booking);
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
        log.info("Проверка, что у item статус доступен available = true");
        if (!item.getAvailable()) {
            throw new BadRequestException("В данный момент вещь не доступна, обратитесь позже");
        }
    }
}
