package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constans.Constants;
import ru.practicum.shareit.item.model.ItemStatus;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingControllerServer.class)
class BookingControllerServerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingDto bookingDto;
    private NewBookingRequest newBookingRequest;
    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(2);

    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setStatus(ItemStatus.WAITING);

        newBookingRequest = new NewBookingRequest();
        newBookingRequest.setItemId(1L);
        newBookingRequest.setStart(start);
        newBookingRequest.setEnd(end);
    }

    @Test
    void createRequest_ShouldReturnBooking() throws Exception {
        when(bookingService.createRequests(eq(1L), any(NewBookingRequest.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(newBookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService).createRequests(eq(1L), any(NewBookingRequest.class));
    }

    @Test
    void updateRequest_ShouldReturnUpdatedBooking() throws Exception {
        bookingDto.setStatus(ItemStatus.APPROVED);
        when(bookingService.updateRequests(eq(1L), eq(1L), eq(true)))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(Constants.USER_ID_HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService).updateRequests(1L, 1L, true);
    }

    @Test
    void getRequest_ShouldReturnBooking() throws Exception {
        when(bookingService.getRequest(eq(1L), eq(1L)))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(bookingService).getRequest(1L, 1L);
    }

    @Test
    void getAllRequest_ShouldReturnListOfBookings() throws Exception {
        when(bookingService.getAllRequest(eq(1L), eq(BookingState.ALL)))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(bookingService).getAllRequest(1L, BookingState.ALL);
    }

    @Test
    void getAllOwnerRequest_ShouldReturnListOfBookings() throws Exception {
        when(bookingService.getAllOwnerRequest(eq(1L), eq("ALL")))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(bookingService).getAllOwnerRequest(1L, "ALL");
    }
}