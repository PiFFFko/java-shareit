package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;

import java.util.List;

public interface BookingService {
    Booking createBooking(Long userId, Booking booking);

    Booking changeApprovedBookingStatus(Long userId, Long bookingId, Boolean approved);

    Booking getBooking(Long userId, Long bookingId);

    List<Booking> getAllBookingsForBooker(Long userId, BookingState state, Long from, Long size);

    List<Booking> getAllBookingsForOwner(Long userId, BookingState state, Long from, Long size);

}
