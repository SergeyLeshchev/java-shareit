package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class UserServiceIT {
    @Autowired
    private UserService userService;
    @Autowired
    private EntityManager entityManager;

    private User petr;

    @BeforeEach
    void addBookingsInDataBase() {
        petr = new User(null, "petr", "petr@email.com");

        entityManager.persist(petr);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void getBookingByIdTest() {
        User actualUser = userService.getUserById(petr.getId());

        assertEquals(petr, actualUser);
    }
}
