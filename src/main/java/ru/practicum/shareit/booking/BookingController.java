package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForPostDto;
import ru.practicum.shareit.booking.exception.UnsupportedBookingStateException;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    private BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody @Valid BookingForPostDto bookingForPostDto) {
        return BookingMapper.toBookingDto(bookingService.createBooking(userId, BookingMapper.toBooking(bookingForPostDto)));
    }

    @PatchMapping("/{bookingId}")
    private BookingDto changeApprovedBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return BookingMapper.toBookingDto(bookingService.changeApprovedBookingStatus(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    private BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long bookingId) {
        return BookingMapper.toBookingDto(bookingService.getBooking(userId, bookingId));
    }

    @GetMapping
    private List<BookingDto> getAllBookingsForBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL", required = false)
                                                     String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedBookingStateException("Unknown state: UNSUPPORTED_STATUS", e);
        }
        return BookingMapper.toListBookingDto(bookingService.getAllBookingsForBooker(userId, bookingState));
    }

    @GetMapping("/owner")
    private List<BookingDto> getAllBookingsForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "ALL", required = false) String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedBookingStateException("Unknown state: UNSUPPORTED_STATUS", e);
        }
        return BookingMapper.toListBookingDto(bookingService.getAllBookingsForOwner(userId, bookingState));
    }


}
