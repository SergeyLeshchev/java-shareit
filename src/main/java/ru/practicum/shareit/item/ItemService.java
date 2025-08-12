package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item);

    Item updateItem(long userId, long itemId, UpdateItemRequest updateItemRequest);

    List<Item> getItemsByUserId(long userId);

    Item getItemById(long itemId);

    void deleteItem(long itemId);

    List<Item> getItemByText(String text);
}
