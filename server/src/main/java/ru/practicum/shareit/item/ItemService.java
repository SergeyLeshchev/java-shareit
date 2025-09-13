package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item, Long userId);

    Item updateItem(Long userId, Long itemId, UpdateItemRequest updateItemRequest);

    List<ItemDto> getItemsByUserId(Long userId);

    ItemDto getItemById(Long itemId);

    void deleteItem(Long userId, Long itemId);

    List<Item> getItemsByText(String text);

    Comment createComment(Long userId, Long itemId, Comment comment);
}
