package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Вызван метод createUser userDto {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Positive @PathVariable Long userId,
                                             @RequestBody UpdateUserRequest updateUserRequest) throws BadRequestException {
        log.info("Вызван метод updateUser updateUserRequest {}", updateUserRequest);
        if (!updateUserRequest.hasName() &&
                !updateUserRequest.hasEmail()) {
            throw new BadRequestException("Для обновления пользователя нужно передать новые данные");
        }
        return userClient.updateUser(userId, updateUserRequest);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Вызван метод getAllUsers");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@Positive @PathVariable Long userId) {
        log.info("Вызван метод getUserById userId {}", userId);
        return userClient.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@Positive @PathVariable Long userId) {
        log.info("Вызван метод deleteUser userId {}", userId);
        userClient.deleteUser(userId);
    }
}
