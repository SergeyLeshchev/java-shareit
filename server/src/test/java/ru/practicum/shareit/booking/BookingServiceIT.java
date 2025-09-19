package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class BookingServiceIT {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private EntityManager entityManager;

    private User petr;
    private ZonedDateTime time;
    private Item apple;
    private Booking bookingApple;


    @BeforeEach
    void addBookingsInDataBase() {
        petr = new User(null, "petr", "petr@email.com");
        time = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);
        apple = new Item(null, "apple", "food", true, petr, null);
        bookingApple = new Booking(null, time.plusHours(2), time.plusHours(3),
                apple, petr, BookingState.APPROVED);

        entityManager.persist(petr);
        entityManager.persist(apple);
        entityManager.persist(bookingApple);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void getBookingByIdTest() {
        Booking actualBooking = bookingService.getBookingById(petr.getId(), bookingApple.getId());

        assertEquals(bookingApple, actualBooking);
    }
}
