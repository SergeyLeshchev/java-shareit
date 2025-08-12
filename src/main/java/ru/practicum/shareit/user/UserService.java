package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(long userId, UpdateUserRequest updateUserRequest);

    List<User> getAllUsers();

    User getUserById(long id);

    void deleteUser(long id);
}
