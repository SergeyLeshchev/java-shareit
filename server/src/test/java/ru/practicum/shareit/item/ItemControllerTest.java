package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Здесь тестируются только успешный сценарий, что при валидном запросе вызывается метод сервиса
// и возвращается статус ответа 200
// Валидация контроллеров тестируется в модуле gateway
// Бизнес-логика, ветвления, значения объектов и полей тестируются в сервисах
@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    // Создаем данные, которые часто используются
    long itemId = 1L;
    long userId = 1L;
    User owner = new User(1L, "userName", "email@email.com");
    Item expectedItem = new Item(1L, "itemName", "itemDescription", false, owner, null);

    @Test
    void createItemTest() throws Exception {
        ItemDto itemDto = new ItemDto(null, "itemName", "itemDescription", true,
                null, null, null, null, null, null);
        when(itemService.createItem(ItemMapper.mapToItem(itemDto), userId, itemDto.getRequestId())).thenReturn(expectedItem);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());

        verify(itemService, times(1))
                .createItem(ItemMapper.mapToItem(itemDto), userId, itemDto.getRequestId());
    }

    @Test
    void updateItemTest() throws Exception {
        UpdateItemRequest updateItemRequest = new UpdateItemRequest("newName", "newDescription", true);
        when(itemService.updateItem(userId, itemId, updateItemRequest)).thenReturn(expectedItem);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateItemRequest)))
                .andExpect(status().isOk());

        verify(itemService, times(1)).updateItem(userId, itemId, updateItemRequest);
    }

    @Test
    void getItemsByUserIdTest() throws Exception {
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).getItemsByUserId(userId);
    }

    @Test
    void getItemByIdTest() throws Exception {
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).getItemById(itemId);
    }

    @Test
    void getItemByTextTest() throws Exception {
        String text = "text";

        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).getItemsByText(text);
    }

    @Test
    void deleteItemTest() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).deleteItem(userId, itemId);
    }

    @Test
    void createCommentTest() throws Exception {
        ItemDto itemDto = ItemMapper.mapToItemDto(expectedItem);
        ZonedDateTime created = ZonedDateTime.now(ZoneOffset.UTC);
        CommentDto commentDto = new CommentDto(null, "textOfComment", itemDto,
                "userName", created.toLocalDateTime());
        Comment expectedComment = new Comment(1L, "textOfComment", expectedItem, owner, created);
        when(itemService.createComment(eq(userId), eq(itemId), any(Comment.class)))
                .thenReturn(expectedComment);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());

        verify(itemService, times(1))
                .createComment(eq(userId), eq(itemId), any(Comment.class));
    }
}