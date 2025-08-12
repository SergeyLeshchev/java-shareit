package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final Map<Long, User> users = new HashMap<>();

    @Override
    public User saveUser(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User updateUser(long userId, User user) {
        return users.put(userId, user);
    }

    @Override
    public List<User> findAllUsers() {
        return users.values().stream().toList();
    }

    @Override
    public User findUserById(long id) {
        return users.get(id);
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }
}
