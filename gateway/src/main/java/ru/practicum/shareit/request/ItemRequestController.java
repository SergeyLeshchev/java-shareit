package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemRequestInDto itemRequestInDto) {
        return itemRequestClient.createItemRequest(userId, itemRequestInDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUser(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getItemRequestsByUser(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@Positive @PathVariable(name = "requestId") Long itemRequestId) {
        return itemRequestClient.getItemRequestById(itemRequestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests() {
        return itemRequestClient.getAllItemRequests();
    }
}
