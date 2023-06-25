package ru.practicum.shareit.booking.service.impl;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.exception.BookingAccessException;
import ru.practicum.shareit.booking.exception.BookingByOwnerOfItemException;
import ru.practicum.shareit.booking.exception.IncorrectTimeOfBookingException;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private final EasyRandom generator = new EasyRandom();
    BookingService bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    Item item;
    User user;
    Booking booking;

    @BeforeEach
    void beforeEach() {
        bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        item = generator.nextObject(Item.class);
        user = generator.nextObject(User.class);
        booking = new Booking();
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        item.setAvailable(Boolean.TRUE);
        booking.setItem(item);
        booking.setBooker(user);
    }

    @Test
    void createBookingSuccess() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> {
                    booking.setId(1L);
                    return booking;
                });
        Booking bookingToSave = bookingService.createBooking(1L, booking);
        assertEquals(item, bookingToSave.getItem());
    }

    @Test
    void createBookingFailStartBeforeEnd() {
        booking.setEnd(LocalDateTime.now().minusDays(1));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        assertThrows(IncorrectTimeOfBookingException.class, () -> bookingService.createBooking(1L, booking));
    }

    @Test
    void createBookingFailItemNotAvailable() {
        item.setAvailable(Boolean.FALSE);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        assertThrows(ItemNotAvailableException.class, () -> bookingService.createBooking(1L, booking));
    }

    @Test
    void createBookingFailCauseBookByOwner() {
        item.setOwner(user);
        booking.setBooker(user);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        assertThrows(BookingByOwnerOfItemException.class, () -> bookingService.createBooking(1L, booking));
    }

    @Test
    void changeApprovedBookingStatusSuccess() {
        Boolean approved = true;
        booking.setId(1L);
        booking.getItem().setOwner(user);
        booking.setStatus(BookingStatus.WAITING);
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking);
        Booking bookingToApprove = bookingService.changeApprovedBookingStatus(user.getId(), booking.getId(), approved);
        assertEquals(BookingStatus.APPROVED, bookingToApprove.getStatus());
        assertEquals(booking.getItem(), bookingToApprove.getItem());
    }

    @Test
    void changeApprovedBookingStatusToRejectedSuccess() {
        Boolean approved = false;
        booking.setId(1L);
        booking.getItem().setOwner(user);
        booking.setStatus(BookingStatus.WAITING);
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking);
        Booking bookingToApprove = bookingService.changeApprovedBookingStatus(user.getId(), booking.getId(), approved);
        assertEquals(BookingStatus.REJECTED, bookingToApprove.getStatus());
        assertEquals(booking.getItem(), bookingToApprove.getItem());
    }

    @Test
    void changeApprovedBookingStatusFailCauseNotWaitingStatus() {
        Boolean approved = true;
        booking.setId(1L);
        booking.getItem().setOwner(user);
        booking.setStatus(BookingStatus.APPROVED);
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        assertThrows(BookingAccessException.class, () -> bookingService.changeApprovedBookingStatus(1L, 1L, approved));
    }


    @Test
    void changeApprovedBookingStatusFailIfNotOwner() {
        Boolean approved = true;
        booking.setId(1L);
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        assertThrows(BookingAccessException.class, () -> bookingService.changeApprovedBookingStatus(1L, 1L, approved));
    }


    @Test
    void getBookingSuccess() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        Booking bookingToGet = bookingService.getBooking(booking.getBooker().getId(), 1L);
        assertEquals(booking.getItem(), bookingToGet.getItem());
    }

    @Test
    void getBookingFailCauseNotOwnerOrBooker() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        assertThrows(BookingAccessException.class, () -> bookingService.getBooking(booking.getBooker().getId() + 1, 1L));
    }

    @Test
    void getAllBookingsForBookerPast() {
        Long userId = 1L;
        BookingState state = BookingState.PAST;
        Long from = 0L;
        Long size = 10L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllPastByUserIdAndSortByDesc(userId, PageRequest.of(0, 10,
                        Sort.by("end").descending())))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getAllBookingsForBooker(userId, state, from, size);
        Assertions.assertEquals(booking, result.get(0));
    }

    @Test
    void getAllBookingsForBookerFuture() {
        Long userId = 1L;
        BookingState state = BookingState.FUTURE;
        Long from = 0L;
        Long size = 10L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllFutureByUserIdAndSortByDesc(userId, PageRequest.of(0, 10,
                        Sort.by("end").descending())))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getAllBookingsForBooker(userId, state, from, size);
        Assertions.assertEquals(booking, result.get(0));
    }

    @Test
    void getAllBookingsForBookerCurrent() {
        Long userId = 1L;
        BookingState state = BookingState.CURRENT;
        Long from = 0L;
        Long size = 10L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllCurrentByUserIdAndSortByDesc(userId, PageRequest.of(0, 10,
                        Sort.by("end").descending())))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getAllBookingsForBooker(userId, state, from, size);
        Assertions.assertEquals(booking, result.get(0));
    }

    @Test
    void getAllBookingsForBookerWaiting() {
        Long userId = 1L;
        BookingState state = BookingState.WAITING;
        Long from = 0L;
        Long size = 10L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllWaitingByUserIdAndSortByDesc(userId, PageRequest.of(0, 10,
                        Sort.by("end").descending())))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getAllBookingsForBooker(userId, state, from, size);
        Assertions.assertEquals(booking, result.get(0));
    }

    @Test
    void getAllBookingsForBookerRejected() {
        Long userId = 1L;
        BookingState state = BookingState.REJECTED;
        Long from = 0L;
        Long size = 10L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllRejectedByUserIdAndSortByDesc(userId, PageRequest.of(0, 10,
                        Sort.by("end").descending())))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getAllBookingsForBooker(userId, state, from, size);
        Assertions.assertEquals(booking, result.get(0));
    }

    @Test
    void getAllBookingsForBookerAll() {
        Long userId = 1L;
        BookingState state = BookingState.ALL;
        Long from = 0L;
        Long size = 10L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllByBookerId(userId, PageRequest.of(0, 10,
                        Sort.by("end").descending())))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getAllBookingsForBooker(userId, state, from, size);
        Assertions.assertEquals(booking, result.get(0));
    }

    @Test
    void getAllBookingsForOwnerPast() {
        Long userId = 1L;
        BookingState state = BookingState.PAST;
        Long from = 0L;
        Long size = 10L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllPastByOwnerIdAndSortByDesc(userId, PageRequest.of(0, 10,
                        Sort.by("end").descending())))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getAllBookingsForOwner(userId, state, from, size);
        Assertions.assertEquals(booking, result.get(0));
    }

    @Test
    void getAllBookingsForOwnerFuture() {
        Long userId = 1L;
        BookingState state = BookingState.FUTURE;
        Long from = 0L;
        Long size = 10L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllFutureByOwnerIdAndSortByDesc(userId, PageRequest.of(0, 10,
                        Sort.by("end").descending())))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getAllBookingsForOwner(userId, state, from, size);
        Assertions.assertEquals(booking, result.get(0));
    }

    @Test
    void getAllBookingsForOwnerCurrent() {
        Long userId = 1L;
        BookingState state = BookingState.CURRENT;
        Long from = 0L;
        Long size = 10L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllCurrentByOwnerIdAndSortByDesc(userId, PageRequest.of(0, 10,
                        Sort.by("end").descending())))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getAllBookingsForOwner(userId, state, from, size);
        Assertions.assertEquals(booking, result.get(0));
    }

    @Test
    void getAllBookingsForOwnerWaiting() {
        Long userId = 1L;
        BookingState state = BookingState.WAITING;
        Long from = 0L;
        Long size = 10L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllWaitingByOwnerIdAndSortByDesc(userId, PageRequest.of(0, 10,
                        Sort.by("end").descending())))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getAllBookingsForOwner(userId, state, from, size);
        Assertions.assertEquals(booking, result.get(0));
    }

    @Test
    void getAllBookingsForOwnerRejected() {
        Long userId = 1L;
        BookingState state = BookingState.REJECTED;
        Long from = 0L;
        Long size = 10L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllRejectedByOwnerIdAndSortByDesc(userId, PageRequest.of(0, 10,
                        Sort.by("end").descending())))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getAllBookingsForOwner(userId, state, from, size);
        Assertions.assertEquals(booking, result.get(0));
    }

    @Test
    void getAllBookingsForOwnerAll() {
        Long userId = 1L;
        BookingState state = BookingState.ALL;
        Long from = 0L;
        Long size = 10L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllByOwnerId(userId, PageRequest.of(0, 10,
                        Sort.by("end").descending())))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getAllBookingsForOwner(userId, state, from, size);
        Assertions.assertEquals(booking, result.get(0));
    }

}