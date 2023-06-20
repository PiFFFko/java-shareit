package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForPostDto;
import ru.practicum.shareit.booking.dto.ShortBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class BookingMapper {

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart().toString(),
                booking.getEnd().toString(),
                booking.getStatus(),
                UserMapper.toShortUser(booking.getBooker()),
                ItemMapper.toShortItem(booking.getItem())
        );
    }

    public ShortBooking toShortBooking(Booking booking) {
        return new ShortBooking(booking.getId(), booking.getBooker().getId());
    }

    public Booking toBooking(BookingForPostDto bookingForPostDto) {
        return new Booking(
                bookingForPostDto.getStart(),
                bookingForPostDto.getEnd(),
                bookingForPostDto.getItemId());
    }

    public List<BookingDto> toListBookingDto(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

}
