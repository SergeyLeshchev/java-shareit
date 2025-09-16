package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.dto.ItemResponse;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ItemRequestMapper {
    public static ItemRequestOutDto maptoItemRequestOutDto(ItemRequest itemRequest) {
        return new ItemRequestOutDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.mapToUserDto(itemRequest.getRequestor()),
                itemRequest.getCreated(),
                null
        );
    }

    public static ItemRequest mapToItemRequest(ItemRequestInDto itemRequestInDto) {
        return new ItemRequest(
                null,
                itemRequestInDto.getDescription(),
                null,
                ZonedDateTime.now(ZoneOffset.UTC)
        );
    }

    public static ItemResponse mapToItemResponse(Item item) {
        return new ItemResponse(item.getId(), item.getName(), item.getOwner().getId());
    }
}
