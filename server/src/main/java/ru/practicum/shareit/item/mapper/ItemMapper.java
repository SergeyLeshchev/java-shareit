package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getOwner(), null, null, List.of(), null);
        if (item.getRequest() != null) {
            itemDto.setRequest(item.getRequest());
        }
        return itemDto;
    }

    public static Item mapToItem(ItemDto itemDto) {
        Item item = new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                null, null);
        if (itemDto.getRequest() != null) {
            item.setRequest(itemDto.getRequest());
        }
        if (itemDto.getOwner() != null) {
            item.setOwner(itemDto.getOwner());
        }
        return item;
    }
}
