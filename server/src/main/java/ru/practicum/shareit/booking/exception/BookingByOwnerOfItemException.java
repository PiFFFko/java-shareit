package ru.practicum.shareit.booking.exception;

public class BookingByOwnerOfItemException extends RuntimeException {
    public BookingByOwnerOfItemException(String message) {
        super(message);
    }
}
