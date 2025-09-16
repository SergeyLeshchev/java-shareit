package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.user.model.User;

import java.time.ZonedDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Здесь тестируются только успешный сценарий, что при валидном запросе вызывается метод сервиса,
// и возвращается статус ответа 200
// Валидация контроллеров тестируется в модуле gateway
// Бизнес-логика, ветвления, значения объектов и полей тестируются в сервисах
@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createItemRequestTest() throws Exception {
        long requestId = 1L;
        long userId = 1L;
        User requestor = new User(userId, "userName", "email@email.com");
        ZonedDateTime created = ZonedDateTime.now();
        ItemRequest expectedItemRequest = new ItemRequest(
                requestId, "ItemRequestDescription", requestor, created);
        ItemRequestInDto itemRequestInDto = new ItemRequestInDto("ItemRequestDescription");
        when(itemRequestService.createItemRequest(eq(userId), any(ItemRequest.class)))
                .thenReturn(expectedItemRequest);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestInDto)))
                .andExpect(status().isOk());

        verify(itemRequestService, times(1))
                .createItemRequest(eq(userId), any(ItemRequest.class));
    }

    @Test
    void getItemRequestsByUserTest() throws Exception {
        long userId = 1L;

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemRequestService, times(1)).getItemRequestsByUser(userId);
    }

    @Test
    void getItemRequestByIdTest() throws Exception {
        long requestId = 1L;

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk());

        verify(itemRequestService, times(1)).getItemRequestById(requestId);
    }

    @Test
    void getAllItemRequestsTest() throws Exception {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk());

        verify(itemRequestService, times(1)).getAllItemRequests();
    }
}