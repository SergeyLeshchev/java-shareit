package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class CommentMapper {
    public static CommentResponseDto mapToCommentResponseDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getText(),
                ItemMapper.mapToItemOutDto(comment.getItem()),
                comment.getAuthor().getName(),
                comment.getCreated().toLocalDateTime()
        );
    }

    public static Comment mapToComment(CommentRequestDto commentDto) {
        return new Comment(
                null,
                commentDto.getText(),
                null,
                null,
                ZonedDateTime.now(ZoneOffset.UTC)
        );
    }
}
