package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto createRequests(Long userId, NewBookingRequest newBookingRequest);

    BookingDto updateRequests(Long userId, Long bookingId, Boolean approved);

    BookingDto getRequest(Long userId, Long bookingId);

    List<BookingDto> getAllRequest(Long userId, BookingState state);

    List<BookingDto> getAllOwnerRequest(Long userId, String state);
}
