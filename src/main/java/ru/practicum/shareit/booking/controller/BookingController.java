package ru.practicum.shareit.booking.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constans.Constants;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createRequest(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID должен быть больше 0") Long userId,
                                    @Validated @RequestBody NewBookingRequest newBookingRequest) {
        return bookingService.createRequests(userId, newBookingRequest);
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingDto updateRequest(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID должен " +
                                            "быть больше 0") Long userId,
                                    @PathVariable @Positive(message = "id должен быть больше 0") Long bookingId,
                                    @RequestParam Boolean approved) {
        return bookingService.updateRequests(userId, bookingId, approved);
    }

    @GetMapping(path = "/{bookingId}")
    public BookingDto getRequest(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID должен " +
            "быть больше 0") Long userId, @PathVariable @Positive(message = "id " +
            "должен быть больше 0") Long bookingId) {
        return bookingService.getRequest(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllRequest(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID" +
            "должен быть больше 0") Long userId, @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getAllRequest(userId, state);
    }

    @GetMapping(path = "/owner")
    public List<BookingDto> getAllOwnerRequest(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message =
            "ID должен быть больше 0") Long userId, @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllOwnerRequest(userId, state);
    }

}
