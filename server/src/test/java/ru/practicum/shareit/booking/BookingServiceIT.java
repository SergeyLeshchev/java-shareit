package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BookingServiceIT {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private User petr;
    private ZonedDateTime time;
    private Item apple;
    private Booking bookingApple;


    @BeforeEach
    void addBookingsInDataBase() {
        petr = new User(null, "petr", "petr@email.com");
        time = ZonedDateTime.now(ZoneOffset.UTC);
        apple = new Item(null, "apple", "food", true, petr, null);
        bookingApple = new Booking(null, time.plusHours(2), time.plusHours(3),
                apple, petr, BookingState.APPROVED);

        userRepository.save(petr);
        itemRepository.save(apple);
        bookingRepository.save(bookingApple);
    }

    @AfterEach
    void clearDataBase() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getBookingByIdTest() {
        Booking expectedBooking = bookingRepository.findById(bookingApple.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));

        Booking actualBooking = bookingService.getBookingById(petr.getId(), bookingApple.getId());

        assertEquals(expectedBooking, actualBooking);
    }
}
