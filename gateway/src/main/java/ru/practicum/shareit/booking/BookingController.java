package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.constans.Constants;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID должен быть больше 0") Long userId,
                                           @Valid @RequestBody BookItemRequestDto bookItemRequestDto) {
        log.info("Creating booking {}, userId={}", bookItemRequestDto, userId);
        return bookingClient.bookItem(userId, bookItemRequestDto);
    }

    @PatchMapping(path = "/{bookingId}")
    public ResponseEntity<Object> updateRequest(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID должен " +
                                                        "быть больше 0") Long userId,
                                                @PathVariable @Positive(message = "id должен быть больше 0") Long bookingId,
                                                @RequestParam Boolean approved) {
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping(path = "/{bookingId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID должен " +
            "быть больше 0") Long userId, @PathVariable @Positive(message = "id " +
            "должен быть больше 0") Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequest(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID" +
            "должен быть больше 0") Long userId, @RequestParam(defaultValue = "ALL") String state) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        return bookingClient.getBookings(userId, bookingState);
    }

    @GetMapping(path = "/owner")
    public ResponseEntity<Object> getAllOwnerRequest(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message =
            "ID должен быть больше 0") Long userId, @RequestParam(defaultValue = "ALL") String state) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        return bookingClient.getOwnerBookings(userId, bookingState);
    }
}
