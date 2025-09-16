package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequest createItemRequest(Long userId, ItemRequest itemRequest);

    ItemRequestOutDto getItemRequestById(Long itemRequestId);

    List<ItemRequest> getAllItemRequests();

    List<ItemRequestOutDto> getItemRequestsByUser(Long userId);
}
