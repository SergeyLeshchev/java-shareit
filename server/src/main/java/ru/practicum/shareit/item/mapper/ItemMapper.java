package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                null,
                List.of(),
                null
        );
        if (item.getOwner() != null) {
            itemDto.setOwner(UserMapper.mapToUserDto(item.getOwner()));
        }
        if (item.getRequest() != null) {
            itemDto.setRequest(ItemRequestMapper.maptoItemRequestOutDto(item.getRequest()));
        }
        return itemDto;
    }

    public static Item mapToItem(ItemDto itemDto) {
        Item item = new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,
                null
        );
        if (itemDto.getOwner() != null) {
            item.setOwner(UserMapper.mapToUser(itemDto.getOwner()));
        }
        return item;
    }
}
