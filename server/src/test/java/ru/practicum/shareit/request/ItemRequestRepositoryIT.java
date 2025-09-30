package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryIT {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User petr;
    private User ivan;
    private ZonedDateTime time;
    private ItemRequest requestApple;
    private ItemRequest requestBall;
    private ItemRequest requestTable;

    @BeforeEach
    void addItemRequestsInDataBase() {
        petr = new User(null, "petr", "petr@email.com");
        ivan = new User(null, "ivan", "ivan@email.com");
        time = ZonedDateTime.now(ZoneOffset.UTC);
        requestApple = new ItemRequest(null, "requestApple", ivan, time);
        requestBall = new ItemRequest(null, "requestBall", petr, time);
        requestTable = new ItemRequest(null, "requestTable", petr, time);

        userRepository.save(petr);
        userRepository.save(ivan);
        itemRequestRepository.save(requestApple);
        itemRequestRepository.save(requestBall);
        itemRequestRepository.save(requestTable);
    }

    @AfterEach
    void clearDataBase() {
        itemRequestRepository.deleteAll();
    }

    @Test
    void findAllByRequestorIdTest() {
        requestBall = itemRequestRepository.findById(requestBall.getId())
                .orElseThrow(() -> new NotFoundException("Запрос с таким id не найден"));
        requestTable = itemRequestRepository.findById(requestTable.getId())
                .orElseThrow(() -> new NotFoundException("Запрос с таким id не найден"));
        List<ItemRequest> expectedItems = List.of(requestBall, requestTable);

        List<ItemRequest> actualItems = itemRequestRepository.findAllByRequestorId(petr.getId());

        assertEquals(expectedItems, actualItems);
    }
}