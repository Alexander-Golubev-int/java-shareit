package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

public interface BookingService {
    BookingDto createRequests(Long userId, NewBookingRequest newBookingRequest);
    BookingDto updateRequests(Long userId, Long bookingId, Boolean approved);
}
