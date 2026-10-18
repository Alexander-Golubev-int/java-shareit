package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemBooking;
import ru.practicum.shareit.item.model.ItemStatus;
import ru.practicum.shareit.user.dto.UserBooking;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemStatus status;
    private UserBooking booker = new UserBooking();
    private ItemBooking item = new ItemBooking();
}
