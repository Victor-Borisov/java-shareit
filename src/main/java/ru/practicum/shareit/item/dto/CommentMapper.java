package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                         .id(comment.getId())
                         .text(comment.getText())
                         .authorName(comment.getAuthor().getName())
                         .created(comment.getCreated())
                         .build();
    }
    public static Comment fromCommentDto(CommentDto commentDto) {
        return Comment.builder()
                      .id(commentDto.getId())
                      .text(commentDto.getText())
                      .created(commentDto.getCreated())
                      .build();
    }
}
