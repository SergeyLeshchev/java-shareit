package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class CommentMapper {
    public static CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                ItemMapper.mapToItemDto(comment.getItem()),
                comment.getAuthor().getName(),
                comment.getCreated().toLocalDateTime()
        );
    }

    public static Comment mapToComment(CommentDto commentDto) {
        return new Comment(
                null,
                commentDto.getText(),
                null,
                null,
                ZonedDateTime.now(ZoneOffset.UTC)
        );
    }
}
