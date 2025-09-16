package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemRequestOutDto {
    private Long id;
    private String description;
    private UserDto requestor;
    private ZonedDateTime created;
    private List<ItemResponse> items;
}
