package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Здесь тестируются только успешный сценарий, что при валидном запросе вызывается метод сервиса
// и возвращается статус ответа 200
// Валидация контроллеров тестируется в модуле gateway
// Бизнес-логика, ветвления, значения объектов и полей тестируются в сервисах
@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // Создаем данные, которые часто используются
    long userId = 1L;
    User expectedUser = new User(1L, "userName", "email@email.com");

    @Test
    void createUserTest() throws Exception {
        UserDto userDto = new UserDto(null, "userName", "email@email.com");
        when(userService.createUser(any(User.class))).thenReturn(expectedUser);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void updateUserTest() throws Exception {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("newName", "newEmail@email.com");
        when(userService.updateUser(userId, updateUserRequest)).thenReturn(expectedUser);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk());

        verify(userService, times(1)).updateUser(userId, updateUserRequest);
    }

    @Test
    void getAllUsersTest() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void findUserByIdTest() throws Exception {
        when(userService.getUserById(userId)).thenReturn(expectedUser);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(userId);
    }
}