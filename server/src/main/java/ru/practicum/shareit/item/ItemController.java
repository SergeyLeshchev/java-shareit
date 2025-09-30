package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemInDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemOutDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemInDto itemDto) {
        Item item = itemService.createItem(ItemMapper.mapToItem(itemDto), userId, itemDto.getRequestId());
        return ItemMapper.mapToItemOutDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemOutDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody UpdateItemRequest updateItemRequest) {
        Item item = itemService.updateItem(userId, itemId, updateItemRequest);
        return ItemMapper.mapToItemOutDto(item);
    }

    @GetMapping
    public List<ItemOutDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemOutDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemOutDto> getItemByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam String text) {
        return itemService.getItemsByText(text).stream()
                .map(ItemMapper::mapToItemOutDto)
                .toList();
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long itemId,
                                            @RequestBody CommentRequestDto commentDto) {
        return CommentMapper.mapToCommentResponseDto(
                itemService.createComment(userId, itemId, CommentMapper.mapToComment(commentDto))
        );
    }
}
