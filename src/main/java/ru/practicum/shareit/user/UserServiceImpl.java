package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User createUser(User user) {
        List<String> emails = repository.findAllUsers().stream()
                .map(User::getEmail)
                .toList();
        if (emails.contains(user.getEmail())) {
            throw new DuplicatedDataException("Пользователь с таким email уже существует");
        }
        return repository.saveUser(user);
    }

    @Override
    public User updateUser(long userId, UpdateUserRequest updateUserRequest) {
        User user = repository.findUserById(userId);
        if (updateUserRequest.hasName()) {
            user.setName(updateUserRequest.getName());
        }
        if (updateUserRequest.hasEmail()) {
            List<User> users = new ArrayList<>(repository.findAllUsers());
            users.remove(user);
            List<String> emails = users.stream()
                    .map(User::getEmail)
                    .toList();
            if (emails.contains(updateUserRequest.getEmail())) {
                throw new DuplicatedDataException("Пользователь с таким email уже существует");
            }
            user.setEmail(updateUserRequest.getEmail());
        }
        return repository.updateUser(userId, user);
    }

    @Override
    public List<User> getAllUsers() {
        return repository.findAllUsers();
    }

    @Override
    public User getUserById(long id) {
        return repository.findUserById(id);
    }

    @Override
    public void deleteUser(long id) {
        repository.deleteUser(id);
    }
}
