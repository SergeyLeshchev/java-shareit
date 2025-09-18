package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item, Long userId, Long itemRequestId);

    Item updateItem(Long userId, Long itemId, UpdateItemRequest updateItemRequest);

    List<ItemOutDto> getItemsByUserId(Long userId);

    ItemOutDto getItemById(Long itemId);

    void deleteItem(Long userId, Long itemId);

    List<Item> getItemsByText(String text);

    Comment createComment(Long userId, Long itemId, Comment comment);
}
