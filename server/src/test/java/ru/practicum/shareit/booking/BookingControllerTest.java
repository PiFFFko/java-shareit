package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingForPostDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();
    @MockBean
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;
    @Autowired
    private MockMvc mvc;
    private Item item;
    private User user;
    private BookingForPostDto bookingForPostDto;
    private EasyRandom generator = new EasyRandom();

    @BeforeEach
    void setUp() {
        item = generator.nextObject(Item.class);
        user = generator.nextObject(User.class);
        bookingForPostDto = new BookingForPostDto();
        bookingForPostDto.setItemId(1L);
        bookingForPostDto.setStart(LocalDateTime.now().plusDays(1));
        bookingForPostDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void createBookingSuccess() throws Exception {
        Mockito.when(bookingService.createBooking(Mockito.anyLong(), Mockito.any(Booking.class)))
                .thenAnswer(invocationOnMock -> {
                    Booking booking = new Booking();
                    booking.setId(1L);
                    booking.setStart(bookingForPostDto.getStart());
                    booking.setEnd(bookingForPostDto.getStart());
                    booking.setStatus(BookingStatus.WAITING);
                    booking.setBooker(user);
                    booking.setItem(item);
                    return booking;
                });

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingForPostDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.name()))
                .andExpect(jsonPath("$.booker").exists())
                .andExpect(jsonPath("$.item").exists());

        Mockito.verify(bookingService).createBooking(Mockito.anyLong(), Mockito.any(Booking.class));
    }

    @Test
    void createBookingFailCauseStartInPast() throws Exception {
        bookingForPostDto.setStart(LocalDateTime.now().minusDays(1));
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingForPostDto)))
                .andExpect(status().is4xxClientError());
        Mockito.verify(bookingService, Mockito.never()).createBooking(Mockito.anyLong(), Mockito.any(Booking.class));
    }

    @Test
    void createBookingFailCauseEndInPast() throws Exception {
        bookingForPostDto.setEnd(LocalDateTime.now().minusDays(1));
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingForPostDto)))
                .andExpect(status().is4xxClientError());
        Mockito.verify(bookingService, Mockito.never()).createBooking(Mockito.anyLong(), Mockito.any(Booking.class));
    }

    @Test
    void setApproveWithValidInputShouldReturnBooking() throws Exception {
        Mockito.when(bookingService.changeApprovedBookingStatus(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenAnswer(invocationOnMock -> {
                    Booking booking = new Booking();
                    booking.setId(1L);
                    booking.setStart(bookingForPostDto.getStart());
                    booking.setEnd(bookingForPostDto.getStart());
                    booking.setStatus(BookingStatus.APPROVED);
                    booking.setBooker(user);
                    booking.setItem(item);
                    return booking;
                });

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(BookingStatus.APPROVED.name()));

        Mockito.verify(bookingService).changeApprovedBookingStatus(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean());
    }

    @Test
    void getBookingSuccess() throws Exception {
        Mockito.when(bookingService.getBooking(Mockito.anyLong(), Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Booking booking = new Booking();
                    booking.setId(1L);
                    booking.setStart(bookingForPostDto.getStart());
                    booking.setEnd(bookingForPostDto.getStart());
                    booking.setStatus(BookingStatus.WAITING);
                    booking.setBooker(user);
                    booking.setItem(item);
                    return booking;
                });
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        Mockito.verify(bookingService).getBooking(Mockito.anyLong(), Mockito.anyLong());

    }

    @Test
    void getBookingsForBookerFailWithWrongState() throws Exception {
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "WRONG")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().is5xxServerError());
        Mockito.verify(bookingService, Mockito.never()).getAllBookingsForBooker(Mockito.anyLong(), Mockito.any(), Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void getBookingsForBookerSuccess() throws Exception {
        List<Booking> expectedBookings = Arrays.asList(generator.nextObject(Booking.class),
                generator.nextObject(Booking.class));
        Mockito.when(bookingService.getAllBookingsForBooker(Mockito.anyLong(), Mockito.any(), Mockito.anyLong(),
                        Mockito.anyLong()))
                .thenReturn(expectedBookings);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.[*].id").exists());
        Mockito.verify(bookingService).getAllBookingsForBooker(Mockito.anyLong(), Mockito.any(), Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void getBookingsForBookerFailWhenWrongFromParameter() throws Exception {
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(-5))
                        .param("size", String.valueOf(10)))
                .andExpect(status().is4xxClientError());
        Mockito.verify(bookingService, Mockito.never()).getAllBookingsForBooker(Mockito.anyLong(), Mockito.any(), Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void getBookingsForBookerFailWhenWrongSizeParameter() throws Exception {
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(-5)))
                .andExpect(status().is4xxClientError());
        Mockito.verify(bookingService, Mockito.never()).getAllBookingsForBooker(Mockito.anyLong(), Mockito.any(), Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void getBookingsForOwnerFailWithWrongState() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "WRONG")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().is5xxServerError());
        Mockito.verify(bookingService, Mockito.never()).getAllBookingsForBooker(Mockito.anyLong(), Mockito.any(), Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void getBookingsForOwnerSuccess() throws Exception {
        List<Booking> expectedBookings = Arrays.asList(generator.nextObject(Booking.class),
                generator.nextObject(Booking.class));
        Mockito.when(bookingService.getAllBookingsForOwner(Mockito.anyLong(), Mockito.any(), Mockito.anyLong(),
                        Mockito.anyLong()))
                .thenReturn(expectedBookings);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.[*].id").exists());
        Mockito.verify(bookingService).getAllBookingsForOwner(Mockito.anyLong(), Mockito.any(), Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void getBookingsForOwnerFailWhenWrongFromParameter() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(-5))
                        .param("size", String.valueOf(10)))
                .andExpect(status().is4xxClientError());
        Mockito.verify(bookingService, Mockito.never()).getAllBookingsForBooker(Mockito.anyLong(), Mockito.any(), Mockito.anyLong(),
                Mockito.anyLong());
    }

    @Test
    void getBookingsForOwnerFailWhenWrongSizeParameter() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(-5)))
                .andExpect(status().is4xxClientError());
        Mockito.verify(bookingService, Mockito.never()).getAllBookingsForBooker(Mockito.anyLong(), Mockito.any(), Mockito.anyLong(),
                Mockito.anyLong());
    }

}