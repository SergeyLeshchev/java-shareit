package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemRequestOutDto {
    private Long id;
    private String description;
    private UserResponseDto requestor;
    private ZonedDateTime created;
    private List<ItemResponse> items;
}
