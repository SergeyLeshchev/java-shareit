package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item saveItem(Item item);

    Item updateItem(Item item);

    List<Item> findItemsByUserId(long userId);

    Item findItemById(long itemId);

    void deleteItem(long itemId);

    List<Item> findItemByText(String text);
}
