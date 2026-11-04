package shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.constans.Constants;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingClient bookingClient;

    @InjectMocks
    private BookingController controller;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void bookItem_ShouldReturnOk() throws Exception {
        BookItemRequestDto request = new BookItemRequestDto();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        when(bookingClient.bookItem(anyLong(), any(BookItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok("{\"id\":1,\"status\":\"WAITING\"}"));

        mvc.perform(post("/bookings")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("WAITING")));

        verify(bookingClient, times(1)).bookItem(eq(1L), any(BookItemRequestDto.class));
    }

    @Test
    void updateRequest_ShouldReturnOk() throws Exception {
        when(bookingClient.updateBooking(anyLong(), anyLong(), eq(true)))
                .thenReturn(ResponseEntity.ok("{\"id\":10,\"status\":\"APPROVED\"}"));

        mvc.perform(patch("/bookings/{bookingId}", 10)
                        .header(Constants.USER_ID_HEADER, 1L)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));

        verify(bookingClient, times(1)).updateBooking(eq(1L), eq(10L), eq(true));
    }

    @Test
    void getRequest_ShouldReturnOk() throws Exception {
        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok("{\"id\":10,\"status\":\"WAITING\"}"));

        mvc.perform(get("/bookings/{bookingId}", 10)
                        .header(Constants.USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)));

        verify(bookingClient, times(1)).getBooking(eq(1L), eq(10L));
    }

    @Test
    void getAllRequest_ShouldReturnOk() throws Exception {
        when(bookingClient.getBookings(anyLong(), eq(BookingState.ALL)))
                .thenReturn(ResponseEntity.ok("[{\"id\":10}]"));

        mvc.perform(get("/bookings")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(10)));

        verify(bookingClient, times(1)).getBookings(eq(1L), eq(BookingState.ALL));
    }

    @Test
    void getAllOwnerRequest_ShouldReturnOk() throws Exception {
        when(bookingClient.getOwnerBookings(anyLong(), eq(BookingState.ALL)))
                .thenReturn(ResponseEntity.ok("[{\"id\":20}]"));

        mvc.perform(get("/bookings/owner")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(20)));

        verify(bookingClient, times(1)).getOwnerBookings(eq(1L), eq(BookingState.ALL));
    }
}