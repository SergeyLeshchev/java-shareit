package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User saveUser(User user);

    User updateUser(long userId, User user);

    List<User> findAllUsers();

    User findUserById(long id);

    void deleteUser(long id);
}