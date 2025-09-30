package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.ZonedDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Здесь тестируются только успешный сценарий, что при валидном запросе вызывается метод сервиса
// и возвращается статус ответа 200
// Валидация контроллеров тестируется в модуле gateway
// Бизнес-логика, ветвления, значения объектов и полей тестируются в сервисах
@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    // Создаем данные, которые часто используются
    long bookingId = 1L;
    long userId = 1L;
    User booker = new User(userId, "userName", "email@email.com");
    Item item = new Item(1L, "itemName", "itemDescription", true, booker, null);
    ZonedDateTime start = ZonedDateTime.now().plusHours(1);
    ZonedDateTime end = ZonedDateTime.now().plusHours(2);
    Booking expectedBooking = new Booking(bookingId, start, end, item, booker, BookingState.WAITING);

    @Test
    void createBookingTest() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L,
                start.toLocalDateTime(), end.toLocalDateTime());
        when(bookingService.createBooking(userId, bookingRequestDto.getItemId(),
                BookingMapper.mapToBooking(userId, bookingRequestDto))).thenReturn(expectedBooking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk());

        verify(bookingService, times(1))
                .createBooking(userId, bookingRequestDto.getItemId(),
                        BookingMapper.mapToBooking(userId, bookingRequestDto));
    }

    @Test
    void updateBookingStateTest() throws Exception {
        Boolean approved = true;
        when(bookingService.updateBookingState(userId, bookingId, approved)).thenReturn(expectedBooking);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).updateBookingState(userId, bookingId, approved);
    }

    @Test
    void getBookingByIdTest() throws Exception {
        when(bookingService.getBookingById(userId, bookingId)).thenReturn(expectedBooking);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).getBookingById(userId, bookingId);
    }

    @Test
    void getAllBookingsByUserTest() throws Exception {
        BookingState state = BookingState.APPROVED;

        mockMvc.perform(get("/bookings")
                        .param("state", "APPROVED")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).getAllBookingsByUser(userId, state);
    }

    @Test
    void getAllBookingsByOwnerTest() throws Exception {
        BookingState state = BookingState.APPROVED;

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "APPROVED")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).getAllBookingsByOwner(userId, state);
    }
}

