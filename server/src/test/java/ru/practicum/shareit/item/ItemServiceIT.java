package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ItemServiceIT {
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private User petr;
    private Item apple;


    @BeforeEach
    void addBookingsInDataBase() {
        petr = new User(null, "petr", "petr@email.com");
        apple = new Item(null, "apple", "food", true, petr, null);

        userRepository.save(petr);
        itemRepository.save(apple);
    }

    @AfterEach
    void clearDataBase() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getBookingByIdTest() {
        Item item = itemRepository.findById(apple.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        ItemOutDto expectedItem = ItemMapper.mapToItemOutDto(item);

        ItemOutDto actualItem = itemService.getItemById(apple.getId());

        assertEquals(expectedItem, actualItem);
    }
}
