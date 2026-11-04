package ru.practicum.shareit.booking.dto.mappers;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemStatus;
import ru.practicum.shareit.user.model.User;

public class RowMapperBooking {
    public static Booking mapToBookingFromNewBookingRequest(NewBookingRequest newBookingRequest,
                                                            Item item, User user) {
        Booking booking = new Booking();
        booking.setStartDate(newBookingRequest.getStart());
        booking.setEndDate(newBookingRequest.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        return booking;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStartDate());
        bookingDto.setEnd(booking.getEndDate());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.getBooker().setId(booking.getBooker().getId());
        bookingDto.getBooker().setName(booking.getBooker().getName());
        bookingDto.getItem().setId((booking.getItem().getId()));
        bookingDto.getItem().setName((booking.getItem().getName()));
        return bookingDto;
    }

    public static void updateBookingStatus(Booking booking, Boolean bool) {
        if (bool) {
            booking.setStatus(ItemStatus.APPROVED);
        } else {
            booking.setStatus(ItemStatus.REJECTED);
        }
    }
}
