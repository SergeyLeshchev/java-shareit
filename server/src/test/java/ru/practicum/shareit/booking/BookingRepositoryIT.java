package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryIT {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private User petr;
    private User ivan;
    private ZonedDateTime time;
    private ItemRequest requestApple;
    private ItemRequest requestBall;
    private ItemRequest requestTable;
    private Item apple;
    private Item ball;
    private Item table;
    private Booking bookingApple;
    private Booking bookingBall;
    private Booking bookingTable;
    private Booking bookingTableIvan;

    @BeforeEach
    void addBookingsInDataBase() {
        petr = new User(null, "petr", "petr@email.com");
        ivan = new User(null, "ivan", "ivan@email.com");
        time = ZonedDateTime.now(ZoneOffset.UTC);
        requestApple = new ItemRequest(null, "requestApple", ivan, time);
        requestBall = new ItemRequest(null, "requestBall", petr, time);
        requestTable = new ItemRequest(null, "requestTable", petr, time);
        apple = new Item(null, "apple", "food", true, ivan, requestApple);
        ball = new Item(null, "ball TexT", "circle", true, petr, requestBall);
        table = new Item(null, "table", "four foots tExt", true, ivan, requestTable);
        bookingApple = new Booking(null, time.plusMinutes(2), time.plusMinutes(3),
                apple, petr, BookingState.APPROVED);
        bookingBall = new Booking(null, time.plusMinutes(4), time.plusMinutes(5),
                ball, ivan, BookingState.APPROVED);
        bookingTable = new Booking(null, time.plusMinutes(6), time.plusMinutes(7),
                table, petr, BookingState.WAITING);
        bookingTableIvan = new Booking(null, time.plusMinutes(8), time.plusMinutes(9),
                table, ivan, BookingState.REJECTED);

        userRepository.save(petr);
        userRepository.save(ivan);
        itemRepository.save(apple);
        itemRepository.save(ball);
        itemRepository.save(table);
        itemRequestRepository.save(requestApple);
        itemRequestRepository.save(requestBall);
        itemRequestRepository.save(requestTable);
        bookingRepository.save(bookingApple);
        bookingRepository.save(bookingBall);
        bookingRepository.save(bookingTable);
        bookingRepository.save(bookingTableIvan);
    }

    @AfterEach
    void clearDataBase() {
        bookingRepository.deleteAll();
    }

    @Test
    void findAllByBookerId() {
        bookingApple = bookingRepository.findById(bookingApple.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        bookingTable = bookingRepository.findById(bookingTable.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        List<Booking> expectedBookings = List.of(bookingApple, bookingTable);

        List<Booking> actualBookings = bookingRepository.findAllByBookerId(petr.getId());

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findAllByBookerIdAndState() {
        bookingApple = bookingRepository.findById(bookingApple.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        List<Booking> expectedBookings = List.of(bookingApple);

        List<Booking> actualBookings = bookingRepository.findAllByBookerIdAndState(
                petr.getId(), bookingApple.getState());

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findAllByItemOwnerId() {
        bookingApple = bookingRepository.findById(bookingApple.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        bookingTable = bookingRepository.findById(bookingTable.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        bookingTableIvan = bookingRepository.findById(bookingTableIvan.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        List<Booking> expectedBookings = List.of(bookingApple, bookingTable, bookingTableIvan);

        List<Booking> actualBookings = bookingRepository.findAllByItemOwnerId(
                bookingApple.getItem().getOwner().getId());

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findAllByItemOwnerIdAndState() {
        bookingApple = bookingRepository.findById(bookingApple.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        List<Booking> expectedBookings = List.of(bookingApple);

        List<Booking> actualBookings = bookingRepository
                .findAllByItemOwnerIdAndState(ivan.getId(), bookingApple.getState());

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findAllByItemId() {
        bookingTable = bookingRepository.findById(bookingTable.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        bookingTableIvan = bookingRepository.findById(bookingTableIvan.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        List<Booking> expectedBookings = List.of(bookingTable, bookingTableIvan);

        List<Booking> actualBookings = bookingRepository.findAllByItemId(bookingTable.getItem().getId());

        assertEquals(expectedBookings, actualBookings);
    }
}