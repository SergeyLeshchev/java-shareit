package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemOutDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private UserResponseDto owner;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<CommentResponseDto> comments;
    private ItemRequestOutDto request;
}
