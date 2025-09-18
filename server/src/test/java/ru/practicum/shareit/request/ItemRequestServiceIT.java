package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ItemRequestServiceIT {
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User petr;
    private ZonedDateTime time;
    private ItemRequest requestApple;

    @BeforeEach
    void addItemRequestsInDataBase() {
        petr = new User(null, "petr", "petr@email.com");
        time = ZonedDateTime.now(ZoneOffset.UTC);
        requestApple = new ItemRequest(null, "requestApple", petr, time);

        userRepository.save(petr);
        itemRequestRepository.save(requestApple);
    }

    @AfterEach
    void clearDataBase() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getBookingByIdTest() {
        ItemRequest itemRequest = itemRequestRepository.findById(requestApple.getId())
                .orElseThrow(() -> new NotFoundException("Запрос с таким id не найден"));
        ItemRequestOutDto expectedItemRequest = ItemRequestMapper.maptoItemRequestOutDto(itemRequest);
        expectedItemRequest.setItems(List.of());

        ItemRequestOutDto actualItemRequest = itemRequestService.getItemRequestById(requestApple.getId());

        assertEquals(expectedItemRequest, actualItemRequest);
    }
}
