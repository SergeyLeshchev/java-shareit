package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        Item item = itemService.createItem(ItemMapper.mapToItem(userId, itemDto));
        return ItemMapper.mapToItemDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable Long itemId,
                              @RequestBody UpdateItemRequest updateItemRequest) {
        Item item = itemService.updateItem(userId, itemId, updateItemRequest);
        return ItemMapper.mapToItemDto(item);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemsByUserId(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                               @PathVariable Long itemId) {
        return ItemMapper.mapToItemDto(itemService.getItemById(itemId));
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam String text) {
        return itemService.getItemByText(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        itemService.deleteItem(itemId);
    }
}
