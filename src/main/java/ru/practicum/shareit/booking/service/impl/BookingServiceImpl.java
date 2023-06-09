package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.exception.BookingAccessException;
import ru.practicum.shareit.booking.exception.BookingByOwnerOfItemException;
import ru.practicum.shareit.booking.exception.BookingUpdateException;
import ru.practicum.shareit.booking.exception.IncorrectTimeOfBookingException;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.item.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Booking createBooking(Long userId, Booking booking) {
        booking.setBooker(userRepository.findById(userId).orElseThrow(() -> new EntityNotExistException("Пользователя не существует")));
        booking.setItem(itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new EntityNotExistException("Предмета не существует")));
        if ((booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().equals(booking.getStart()))) {
            throw new IncorrectTimeOfBookingException("Неправильно указано время");
        }
        if (booking.getItem().getAvailable().equals(false)) {
            throw new ItemNotAvailableException("Предмет не доступен");
        }
        if (booking.getBooker().getId().equals(booking.getItem().getOwner().getId())) {
            throw new BookingByOwnerOfItemException("Вы являетесь пользователем вещи");
        }
        booking.setStatus(BookingStatus.WAITING);
        Booking bookingToSave = bookingRepository.save(booking);
        return bookingToSave;
    }

    @Override
    public Booking changeApprovedBookingStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotExistException("Бронирования не существует"));
        if (booking.getItem().getOwner().getId().equals(userId)) {
            if (booking.getStatus().equals(BookingStatus.WAITING)) {
                booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
                bookingRepository.save(booking);
                return booking;
            }
            throw new BookingUpdateException("У бронирования уже установлен статус");
        }
        throw new BookingAccessException("Изменять статуса запроса на бронирование может только владелец");
    }

    @Override
    public Booking getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotExistException("Бронирований не существует"));
        if (userId.equals(booking.getBooker().getId()) || userId.equals(booking.getItem().getOwner().getId())) {
            return booking;
        }
        throw new BookingAccessException("Запрашивать букинг может только владелец вещи или создатель запроса");

    }

    @Override
    public List<Booking> getAllBookingsForBooker(Long userId, BookingState state) {
        List<Booking> bookings;
        userRepository.findById(userId).orElseThrow(() -> new EntityNotExistException("пользователя не существует"));
        switch (state) {
            case ALL: {
                bookings = bookingRepository.findAllByBookerId(userId);
                break;
            }
            case PAST: {
                bookings = bookingRepository.findAllPastByUserIdAndSortByDesc(userId);
                break;
            }
            case FUTURE: {
                bookings = bookingRepository.findAllFutureByUserIdAndSortByDesc(userId);
                break;
            }
            case CURRENT: {
                bookings = bookingRepository.findAllCurrentByUserIdAndSortByDesc(userId);
                break;
            }
            case WAITING: {
                bookings = bookingRepository.findAllWaitingByUserIdAndSortByDesc(userId);
                break;
            }
            case REJECTED: {
                bookings = bookingRepository.findAllRejectedByUserIdAndSortByDesc(userId);
                break;
            }
            default: {
                bookings = Collections.emptyList();
                break;
            }
        }
        return bookings;
    }

    @Override
    public List<Booking> getAllBookingsForOwner(Long userId, BookingState state) {
        List<Booking> bookings;
        userRepository.findById(userId).orElseThrow(() -> new EntityNotExistException("пользователя не существует"));
        switch (state) {
            case ALL: {
                bookings = bookingRepository.findAllByOwnerId(userId);
                break;
            }
            case PAST: {
                bookings = bookingRepository.findAllPastByOwnerIdAndSortByDesc(userId);
                break;
            }
            case FUTURE: {
                bookings = bookingRepository.findAllFutureByOwnerIdAndSortByDesc(userId);
                break;
            }
            case CURRENT: {
                bookings = bookingRepository.findAllCurrentByOwnerIdAndSortByDesc(userId);
                break;
            }
            case WAITING: {
                bookings = bookingRepository.findAllWaitingByOwnerIdAndSortByDesc(userId);
                break;
            }
            case REJECTED: {
                bookings = bookingRepository.findAllRejectedByOwnerIdAndSortByDesc(userId);
                break;
            }
            default: {
                bookings = Collections.emptyList();
                break;
            }
        }
        return bookings;
    }
}
