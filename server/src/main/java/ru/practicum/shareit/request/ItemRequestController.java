package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestOutDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestBody ItemRequestInDto itemRequestInDto) {
        ItemRequest itemRequest = itemRequestService.createItemRequest(userId,
                ItemRequestMapper.mapToItemRequest(itemRequestInDto));
        return ItemRequestMapper.maptoItemRequestOutDto(itemRequest);
    }

    @GetMapping
    public List<ItemRequestOutDto> getItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getItemRequestsByUser(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestOutDto getItemRequestById(@PathVariable(name = "requestId") Long itemRequestId) {
        return itemRequestService.getItemRequestById(itemRequestId);
    }

    @GetMapping("/all")
    public List<ItemRequestOutDto> getAllItemRequests() {
        return itemRequestService.getAllItemRequests().stream()
                .map(ItemRequestMapper::maptoItemRequestOutDto)
                .toList();
    }
}
