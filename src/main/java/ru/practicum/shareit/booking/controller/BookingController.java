package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constans.Constants;
import ru.practicum.shareit.user.dto.UserDto;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createRequests(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID должен быть больше 0") Long userId,
                                 @Validated @RequestBody NewBookingRequest newBookingRequest) {
        return bookingService.createRequests(userId, newBookingRequest);
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingDto updateRequests(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID должен " +
                                           "быть больше 0") Long userId,
                              @PathVariable @Positive(message = "id должен быть больше 0") Long bookingId,
                              @RequestParam Boolean approved) {
        return bookingService.updateRequests(userId, bookingId, approved);
    }
}
