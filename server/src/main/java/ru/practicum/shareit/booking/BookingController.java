package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForPostDto;
import ru.practicum.shareit.booking.exception.UnsupportedBookingStateException;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.InvalidParameterException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String SHARER_USER_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestHeader(SHARER_USER_HEADER) Long userId,
                                                      @RequestBody @Valid BookingForPostDto bookingForPostDto) {
        return ResponseEntity.ok().body(BookingMapper.toBookingDto(bookingService.createBooking(userId, BookingMapper.toBooking(bookingForPostDto))));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> changeApprovedBookingStatus(@RequestHeader(SHARER_USER_HEADER) Long userId, @PathVariable Long bookingId, @RequestParam boolean approved) {
        return ResponseEntity.ok().body(BookingMapper.toBookingDto(bookingService.changeApprovedBookingStatus(userId, bookingId, approved)));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {
        return ResponseEntity.ok().body(BookingMapper.toBookingDto(bookingService.getBooking(userId, bookingId)));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookingsForBooker(@RequestHeader(SHARER_USER_HEADER) Long userId,
                                                                    @RequestParam(defaultValue = "ALL", required = false) String state,
                                                                    @RequestParam(defaultValue = "0") Long from,
                                                                    @RequestParam(defaultValue = "5") Long size) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedBookingStateException("Unknown state: UNSUPPORTED_STATUS", e);
        }
        if (from < 0) {
            throw new InvalidParameterException("Индекс первого элемента не может быть меньше 0");
        }
        if (size <= 0) {
            throw new InvalidParameterException("Количество элементов для отображения должно быть больше 0");
        }
        return ResponseEntity.ok().body(BookingMapper.toListBookingDto(bookingService.getAllBookingsForBooker(userId, bookingState, from, size)));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllBookingsForOwner(@RequestHeader(SHARER_USER_HEADER) Long userId,
                                                 @RequestParam(defaultValue = "ALL", required = false) String state,
                                                 @RequestParam(defaultValue = "0") Long from,
                                                 @RequestParam(defaultValue = "5") Long size) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedBookingStateException("Unknown state: UNSUPPORTED_STATUS", e);
        }
        if (from < 0) {
            throw new InvalidParameterException("Индекс первого элемента не может быть меньше 0");
        }
        if (size <= 0) {
            throw new InvalidParameterException("Количество элементов для отображения должно быть больше 0");
        }
        return ResponseEntity.ok().body(BookingMapper.toListBookingDto(bookingService.getAllBookingsForOwner(userId, bookingState, from, size)));
    }


}
