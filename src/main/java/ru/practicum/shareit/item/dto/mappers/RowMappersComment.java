package ru.practicum.shareit.item.dto.mappers;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class RowMappersComment {
    public static Comment createComment(User user, Item item, String text) {
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setItem(item);
        comment.setText(text);
        return comment;
    }

    public static CommentDto mapToCommentDto(Comment comment, User user) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(user.getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }
}
