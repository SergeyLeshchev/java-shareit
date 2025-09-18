package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserServiceIT {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private User petr;

    @BeforeEach
    void addBookingsInDataBase() {
        petr = new User(null, "petr", "petr@email.com");

        userRepository.save(petr);
    }

    @AfterEach
    void clearDataBase() {
        userRepository.deleteAll();
    }

    @Test
    void getBookingByIdTest() {
        User expectedUser = userRepository.findById(petr.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));

        User actualUser = userService.getUserById(petr.getId());

        assertEquals(expectedUser, actualUser);
    }

}
