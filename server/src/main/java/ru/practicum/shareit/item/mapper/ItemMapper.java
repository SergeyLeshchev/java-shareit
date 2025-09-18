package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemInDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

public class ItemMapper {
    public static ItemOutDto mapToItemOutDto(Item item) {
        ItemOutDto itemOutDto = new ItemOutDto(
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
            itemOutDto.setOwner(UserMapper.mapToUserResponseDto(item.getOwner()));
        }
        if (item.getRequest() != null) {
            itemOutDto.setRequest(ItemRequestMapper.maptoItemRequestOutDto(item.getRequest()));
        }
        return itemOutDto;
    }

    public static Item mapToItem(ItemInDto itemDto) {
        return new Item(
                null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,
                null
        );
    }
}
