package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class CommentResponseDto {
    private Long id;
    private String text;
    private ItemOutDto item;
    private String authorName;
    private LocalDateTime created;
}