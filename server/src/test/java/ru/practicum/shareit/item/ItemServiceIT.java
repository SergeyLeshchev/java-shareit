package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class ItemServiceIT {
    @Autowired
    private ItemService itemService;
    @Autowired
    private EntityManager entityManager;

    private User petr;
    private Item apple;


    @BeforeEach
    void addBookingsInDataBase() {
        petr = new User(null, "petr", "petr@email.com");
        apple = new Item(null, "apple", "food", true, petr, null);

        entityManager.persist(petr);
        entityManager.persist(apple);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void getBookingByIdTest() {
        ItemOutDto expectedItem = ItemMapper.mapToItemOutDto(apple);

        ItemOutDto actualItem = itemService.getItemById(apple.getId());

        assertEquals(expectedItem, actualItem);
    }
}
