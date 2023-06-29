package ru.practicum.shareit.item.exception;

public class CommentByNotBookerException extends RuntimeException {
    public CommentByNotBookerException(String message) {
        super(message);
    }
}
