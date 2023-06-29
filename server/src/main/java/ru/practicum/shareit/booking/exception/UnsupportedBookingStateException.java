package ru.practicum.shareit.booking.exception;

public class UnsupportedBookingStateException extends RuntimeException {

    public UnsupportedBookingStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
