package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class ItemRequestServiceIT {
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private EntityManager entityManager;

    private User petr;
    private ZonedDateTime time;
    private ItemRequest requestApple;

    @BeforeEach
    void addItemRequestsInDataBase() {
        petr = new User(null, "petr", "petr@email.com");
        time = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);
        requestApple = new ItemRequest(null, "requestApple", petr, time);

        entityManager.persist(petr);
        entityManager.persist(requestApple);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void getBookingByIdTest() {
        ItemRequestOutDto expectedItemRequest = ItemRequestMapper.maptoItemRequestOutDto(requestApple);
        expectedItemRequest.setItems(List.of());

        ItemRequestOutDto actualItemRequest = itemRequestService.getItemRequestById(requestApple.getId());

        assertEquals(expectedItemRequest, actualItemRequest);
    }
}
