package ru.practicum.shareit.booking.exception;

public class IncorrectTimeOfBookingException extends RuntimeException {
    public IncorrectTimeOfBookingException(String message) {
        super(message);
    }
}
