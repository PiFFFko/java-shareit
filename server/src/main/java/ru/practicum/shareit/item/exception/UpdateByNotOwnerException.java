package ru.practicum.shareit.item.exception;

public class UpdateByNotOwnerException extends RuntimeException {
    public UpdateByNotOwnerException(String message) {
        super(message);
    }
}
