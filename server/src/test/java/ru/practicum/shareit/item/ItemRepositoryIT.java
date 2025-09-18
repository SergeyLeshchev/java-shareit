package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.exception.NotFoundException;
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
class ItemRepositoryIT {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User petr;
    private User ivan;
    private ZonedDateTime time;
    private ItemRequest requestApple;
    private ItemRequest requestBall;
    private Item apple;
    private Item ball;
    private Item table;

    @BeforeEach
    void addItemsInDataBase() {
        petr = new User(null, "petr", "petr@email.com");
        ivan = new User(null, "ivan", "ivan@email.com");
        time = ZonedDateTime.now(ZoneOffset.UTC);
        requestApple = new ItemRequest(null, "requestApple", petr, time);
        requestBall = new ItemRequest(null, "requestList", petr, time);
        apple = new Item(null, "apple", "food", true, ivan, requestBall);
        ball = new Item(null, "ball TexT", "circle", true, petr, requestBall);
        table = new Item(null, "table", "four foots tExt", true, ivan, requestApple);

        userRepository.save(petr);
        userRepository.save(ivan);
        itemRequestRepository.save(requestApple);
        itemRequestRepository.save(requestBall);
        itemRepository.save(apple);
        itemRepository.save(ball);
        itemRepository.save(table);
    }

    @AfterEach
    void clearDataBase() {
        itemRepository.deleteAll();
    }

    @Test
    void findItemsByTextTest() {
        ball = itemRepository.findById(ball.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        table = itemRepository.findById(table.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        List<Item> expectedItems = List.of(ball, table);

        List<Item> actualItems = itemRepository.findItemsByText("teXT");

        assertEquals(expectedItems, actualItems);
    }

    @Test
    void findAllByOwnerIdTest() {
        apple = itemRepository.findById(apple.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        table = itemRepository.findById(table.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        List<Item> expectedItems = List.of(apple, table);

        List<Item> actualItems = itemRepository.findAllByOwnerId(ivan.getId());

        assertEquals(expectedItems, actualItems);
    }

    @Test
    void findAllByRequestIdTest() {
        apple = itemRepository.findById(apple.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        ball = itemRepository.findById(ball.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        List<Item> expectedItems = List.of(apple, ball);

        List<Item> actualItems = itemRepository.findAllByRequestId(requestBall.getId());

        assertEquals(expectedItems, actualItems);
    }

    @Test
    void findAllByRequestIdInTest() {
        apple = itemRepository.findById(apple.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        ball = itemRepository.findById(ball.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        List<Item> expectedItems = List.of(apple, ball);

        List<Item> actualItems = itemRepository.findAllByRequestIdIn(List.of(requestBall.getId()));

        assertEquals(expectedItems, actualItems);
    }
}