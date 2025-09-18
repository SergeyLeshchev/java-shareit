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

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    // Объекты, которые не должны оказаться в списке actualList
    private User badUser;
    private ItemRequest badItemRequest;
    private Item badItem;
    private Booking badBookingPast;
    private Booking badBookingCurrent;
    private Booking badBookingFuture;

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


        // Для FUTURE
        bookingApple = new Booking(null, time.plusHours(2), time.plusHours(3),
                apple, petr, BookingState.APPROVED);
        bookingBall = new Booking(null, time.plusHours(4), time.plusHours(5),
                ball, ivan, BookingState.APPROVED);
        bookingTable = new Booking(null, time.plusHours(6), time.plusHours(7),
                table, petr, BookingState.WAITING);
        bookingTableIvan = new Booking(null, time.plusHours(8), time.plusHours(9),
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

        // Добавляем объекты, которые не должны оказаться в списке actualList
        badUser = new User(null, "badUserName", "badUser@email.com");
        badItemRequest = new ItemRequest(null, "badRequestDescription", badUser, time);
        badItem = new Item(null, "badItemName", "badItemDescription",
                true, badUser, badItemRequest);
        badBookingPast = new Booking(null, time.minusHours(4), time.minusHours(3),
                badItem, badUser, BookingState.APPROVED);
        badBookingCurrent = new Booking(null, time.minusHours(2), time.plusHours(3),
                badItem, badUser, BookingState.APPROVED);
        badBookingFuture = new Booking(null, time.plusHours(2), time.plusHours(3),
                badItem, badUser, BookingState.APPROVED);

        userRepository.save(badUser);
        itemRepository.save(badItem);
        itemRequestRepository.save(badItemRequest);
        bookingRepository.save(badBookingPast);
        bookingRepository.save(badBookingCurrent);
        bookingRepository.save(badBookingFuture);
    }

    @AfterEach
    void clearDataBase() {
        bookingRepository.deleteAll();
    }

    @Test
    void findAllByBookerIdOrderByStartDesc() {
        bookingApple = bookingRepository.findById(bookingApple.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        bookingTable = bookingRepository.findById(bookingTable.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        List<Booking> expectedBookings = List.of(bookingTable, bookingApple);

        List<Booking> actualBookings = bookingRepository.findAllByBookerIdOrderByStartDesc(petr.getId());

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findAllByBookerIdAndStateOrderByStartDesc() {
        bookingApple = bookingRepository.findById(bookingApple.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        List<Booking> expectedBookings = List.of(bookingApple);

        List<Booking> actualBookings = bookingRepository.findAllByBookerIdAndStateOrderByStartDesc(
                petr.getId(), bookingApple.getState());

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findAllByBookerIdAndEndBeforeOrderByStartDesc() {
        // Для PAST
        bookingApple.setStart(time.minusHours(2));
        bookingApple.setEnd(time.minusHours(3));
        bookingBall.setStart(time.minusHours(4));
        bookingBall.setEnd(time.minusHours(5));
        bookingTable.setStart(time.minusHours(6));
        bookingTable.setEnd(time.minusHours(7));
        bookingTableIvan.setStart(time.minusHours(8));
        bookingTableIvan.setEnd(time.minusHours(9));

        bookingRepository.save(bookingApple);
        bookingRepository.save(bookingBall);
        bookingRepository.save(bookingTable);
        bookingRepository.save(bookingTableIvan);

        bookingApple = bookingRepository.findById(bookingApple.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        bookingTable = bookingRepository.findById(bookingTable.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        List<Booking> expectedBookings = List.of(bookingApple, bookingTable);

        List<Booking> actualBookings = bookingRepository
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(petr.getId(), time);

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        // Для CURRENT
        bookingApple.setStart(time.minusHours(2));
        bookingBall.setStart(time.minusHours(4));
        bookingTable.setStart(time.minusHours(6));
        bookingTableIvan.setStart(time.minusHours(8));

        bookingRepository.save(bookingApple);
        bookingRepository.save(bookingBall);
        bookingRepository.save(bookingTable);
        bookingRepository.save(bookingTableIvan);

        bookingApple = bookingRepository.findById(bookingApple.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        bookingTable = bookingRepository.findById(bookingTable.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        List<Booking> expectedBookings = List.of(bookingApple, bookingTable);

        List<Booking> actualBookings = bookingRepository
                .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(petr.getId(), time, time);

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findAllByBookerIdAndStartAfterOrderByStartDesc() {
        bookingApple = bookingRepository.findById(bookingApple.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        bookingTable = bookingRepository.findById(bookingTable.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        List<Booking> expectedBookings = List.of(bookingTable, bookingApple);

        List<Booking> actualBookings = bookingRepository
                .findAllByBookerIdAndStartAfterOrderByStartDesc(petr.getId(), time);

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDesc() {
        bookingApple = bookingRepository.findById(bookingApple.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        bookingTable = bookingRepository.findById(bookingTable.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        bookingTableIvan = bookingRepository.findById(bookingTableIvan.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        List<Booking> expectedBookings = List.of(bookingTableIvan, bookingTable, bookingApple);

        List<Booking> actualBookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(
                bookingApple.getItem().getOwner().getId());

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findAllByItemOwnerIdAndStateOrderByStartDesc() {
        bookingApple = bookingRepository.findById(bookingApple.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        List<Booking> expectedBookings = List.of(bookingApple);

        List<Booking> actualBookings = bookingRepository
                .findAllByItemOwnerIdAndStateOrderByStartDesc(ivan.getId(), bookingApple.getState());

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findAllByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        // Для PAST
        bookingApple.setStart(time.minusHours(9));
        bookingApple.setEnd(time.minusHours(8));
        bookingBall.setStart(time.minusHours(7));
        bookingBall.setEnd(time.minusHours(6));
        bookingTable.setStart(time.minusHours(5));
        bookingTable.setEnd(time.minusHours(4));
        bookingTableIvan.setStart(time.minusHours(3));
        bookingTableIvan.setEnd(time.minusHours(2));

        bookingRepository.save(bookingApple);
        bookingRepository.save(bookingBall);
        bookingRepository.save(bookingTable);
        bookingRepository.save(bookingTableIvan);

        bookingApple = bookingRepository.findById(bookingApple.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        bookingTable = bookingRepository.findById(bookingTable.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        bookingTableIvan = bookingRepository.findById(bookingTableIvan.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        List<Booking> expectedBookings = List.of(bookingTableIvan, bookingTable, bookingApple);

        List<Booking> actualBookings = bookingRepository
                .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(bookingApple.getItem().getOwner().getId(), time);

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        // Для CURRENT
        bookingApple.setStart(time.minusHours(8));
        bookingBall.setStart(time.minusHours(6));
        bookingTable.setStart(time.minusHours(4));
        bookingTableIvan.setStart(time.minusHours(2));

        bookingRepository.save(bookingApple);
        bookingRepository.save(bookingBall);
        bookingRepository.save(bookingTable);
        bookingRepository.save(bookingTableIvan);

        bookingApple = bookingRepository.findById(bookingApple.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        bookingTable = bookingRepository.findById(bookingTable.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        bookingTableIvan = bookingRepository.findById(bookingTableIvan.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        List<Booking> expectedBookings = List.of(bookingTableIvan, bookingTable, bookingApple);

        List<Booking> actualBookings = bookingRepository
                .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        bookingApple.getItem().getOwner().getId(), time, time);

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
        bookingApple = bookingRepository.findById(bookingApple.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        bookingTable = bookingRepository.findById(bookingTable.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        bookingTableIvan = bookingRepository.findById(bookingTableIvan.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        List<Booking> expectedBookings = List.of(bookingTableIvan, bookingTable, bookingApple);

        List<Booking> actualBookings = bookingRepository
                .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        bookingApple.getItem().getOwner().getId(), time);

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findAllByItemIdOrderByStartDesc() {
        bookingTable = bookingRepository.findById(bookingTable.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        bookingTableIvan = bookingRepository.findById(bookingTableIvan.getId())
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        List<Booking> expectedBookings = List.of(bookingTableIvan, bookingTable);

        List<Booking> actualBookings = bookingRepository
                .findAllByItemIdOrderByStartDesc(bookingTable.getItem().getId());

        assertEquals(expectedBookings, actualBookings);
    }
}