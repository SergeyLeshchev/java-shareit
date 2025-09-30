package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserResponseDto createUser(@RequestBody UserRequestDto userDto) {
        User user = userService.createUser(UserMapper.mapToUser(userDto));
        return UserMapper.mapToUserResponseDto(user);
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(@PathVariable Long userId,
                                      @RequestBody UpdateUserRequest updateUserRequest) {
        User user = userService.updateUser(userId, updateUserRequest);
        return UserMapper.mapToUserResponseDto(user);
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(UserMapper::mapToUserResponseDto)
                .toList();
    }

    @GetMapping("/{userId}")
    public UserResponseDto findUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return UserMapper.mapToUserResponseDto(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
