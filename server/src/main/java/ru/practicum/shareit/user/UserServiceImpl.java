package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User createUser(User user) {
        List<String> emails = repository.findAll().stream()
                .map(User::getEmail)
                .toList();
        if (emails.contains(user.getEmail())) {
            throw new DuplicatedDataException("Пользователь с таким email уже существует");
        }
        return repository.save(user);
    }

    @Override
    public User updateUser(long userId, UpdateUserRequest updateUserRequest) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь"));
        if (updateUserRequest.hasName()) {
            user.setName(updateUserRequest.getName());
        }
        if (updateUserRequest.hasEmail()) {
            List<User> users = new ArrayList<>(repository.findAll());
            users.remove(user);
            List<String> emails = users.stream()
                    .map(User::getEmail)
                    .toList();
            if (emails.contains(updateUserRequest.getEmail())) {
                throw new DuplicatedDataException("Пользователь с таким email уже существует");
            }
            user.setEmail(updateUserRequest.getEmail());
        }
        return repository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User getUserById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь"));
    }

    @Override
    public void deleteUser(long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь"));
        repository.delete(user);
    }
}
