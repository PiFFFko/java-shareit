package ru.practicum.shareit.booking.repository;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
})
@ExtendWith(SpringExtension.class)
class BookingRepositoryTest {

    private EasyRandom generator = new EasyRandom();
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    private Item item;
    private Item item2;
    private User user;
    private User user2;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        user = generator.nextObject(User.class);
        user2 = generator.nextObject(User.class);
        item = generator.nextObject(Item.class);
        item2 = generator.nextObject(Item.class);
        booking = generator.nextObject(Booking.class);
        user.setId(null);
        user2.setId(null);
        item.setId(null);
        item.setRequest(null);
        item2.setId(null);
        item2.setRequest(null);
        booking.setId(null);
        item.setOwner(user);
        item2.setOwner(user);
        booking.setBooker(user2);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item2);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void findAllWaitingByUserIdAndSortByDesc() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest sortedByEndDesc = PageRequest.of(0, 5, Sort.by("end").descending());

        List<Booking> optionalBooking = bookingRepository
                .findAllWaitingByUserIdAndSortByDesc(user2.getId(), sortedByEndDesc);
        Assertions.assertFalse(optionalBooking.isEmpty());
        Assertions.assertEquals(booking, optionalBooking.get(0));
    }

    @Test
    void findAllWaitingByOwnerIdAndSortByDesc() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest sortedByEndDesc = PageRequest.of(0, 5, Sort.by("end").descending());

        List<Booking> optionalBooking = bookingRepository
                .findAllWaitingByOwnerIdAndSortByDesc(user.getId(), sortedByEndDesc);
        Assertions.assertFalse(optionalBooking.isEmpty());
        Assertions.assertEquals(booking, optionalBooking.get(0));
    }

    @Test
    void findAllRejectedByUserIdAndSortByDesc() {
        booking.setStatus(BookingStatus.REJECTED);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest sortedByEndDesc = PageRequest.of(0, 5, Sort.by("end").descending());

        List<Booking> optionalBooking = bookingRepository
                .findAllRejectedByUserIdAndSortByDesc(user2.getId(), sortedByEndDesc);
        Assertions.assertFalse(optionalBooking.isEmpty());
        Assertions.assertEquals(booking, optionalBooking.get(0));
    }

    @Test
    void findAllRejectedByOwnerIdAndSortByDesc() {
        booking.setStatus(BookingStatus.REJECTED);
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest sortedByEndDesc = PageRequest.of(0, 5, Sort.by("end").descending());

        List<Booking> optionalBooking = bookingRepository
                .findAllRejectedByOwnerIdAndSortByDesc(user.getId(), sortedByEndDesc);
        Assertions.assertFalse(optionalBooking.isEmpty());
        Assertions.assertEquals(booking, optionalBooking.get(0));
    }

    @Test
    void findAllCurrentByUserIdAndSortByDesc() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest sortedByEndDesc = PageRequest.of(0, 5, Sort.by("end").descending());

        List<Booking> optionalBooking = bookingRepository
                .findAllCurrentByUserIdAndSortByDesc(user2.getId(), sortedByEndDesc);
        Assertions.assertFalse(optionalBooking.isEmpty());
        Assertions.assertEquals(booking, optionalBooking.get(0));
    }

    @Test
    void findAllCurrentByOwnerIdAndSortByDesc() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest sortedByEndDesc = PageRequest.of(0, 5, Sort.by("end").descending());

        List<Booking> optionalBooking = bookingRepository
                .findAllCurrentByOwnerIdAndSortByDesc(user.getId(), sortedByEndDesc);
        Assertions.assertFalse(optionalBooking.isEmpty());
        Assertions.assertEquals(booking, optionalBooking.get(0));
    }

    @Test
    void findAllPastByUserIdAndSortByDesc() {
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(booking.getStart().plusHours(10));
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest sortedByEndDesc = PageRequest.of(0, 5, Sort.by("end").descending());

        List<Booking> optionalBooking = bookingRepository
                .findAllPastByUserIdAndSortByDesc(user2.getId(), sortedByEndDesc);
        Assertions.assertFalse(optionalBooking.isEmpty());
        Assertions.assertEquals(booking, optionalBooking.get(0));
    }

    @Test
    void findAllPastByOwnerIdAndSortByDesc() {
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(booking.getStart().plusHours(10));
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest sortedByEndDesc = PageRequest.of(0, 5, Sort.by("end").descending());

        List<Booking> optionalBooking = bookingRepository
                .findAllPastByOwnerIdAndSortByDesc(user.getId(), sortedByEndDesc);
        Assertions.assertFalse(optionalBooking.isEmpty());
        Assertions.assertEquals(booking, optionalBooking.get(0));
    }

    @Test
    void findAllFutureByUserIdAndSortByDesc() {
        booking.setStart(LocalDateTime.now().plusDays(25));
        booking.setEnd(LocalDateTime.now().plusDays(30));
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        ;
        PageRequest sortedByEndDesc = PageRequest.of(0, 5, Sort.by("end").descending());

        List<Booking> optionalBooking = bookingRepository
                .findAllFutureByUserIdAndSortByDesc(user2.getId(), sortedByEndDesc);
        Assertions.assertFalse(optionalBooking.isEmpty());
        Assertions.assertEquals(booking, optionalBooking.get(0));
    }

    @Test
    void findAllFutureByOwnerIdAndSortByDesc() {
        booking.setStart(LocalDateTime.now().plusDays(25));
        booking.setEnd(LocalDateTime.now().plusDays(30));
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        PageRequest sortedByEndDesc = PageRequest.of(0, 5, Sort.by("end").descending());

        List<Booking> optionalBooking = bookingRepository
                .findAllFutureByOwnerIdAndSortByDesc(user.getId(), sortedByEndDesc);
        Assertions.assertFalse(optionalBooking.isEmpty());
        Assertions.assertEquals(booking, optionalBooking.get(0));
    }

    @Test
    void findLastBookingByItemId() {
        em.persist(user);
        em.persist(user2);
        em.persist(item2);
        em.persist(booking);
        List<Booking> optionalBooking = bookingRepository.findLastBookingForItem_Id(item2.getId());
        Assertions.assertFalse(optionalBooking.isEmpty());
        Assertions.assertEquals(booking, optionalBooking.get(0));
    }

    @Test
    void findNextBookingByItemId() {
        booking.setStart(LocalDateTime.now().plusDays(25));
        booking.setEnd(LocalDateTime.now().plusDays(30));
        em.persist(user);
        em.persist(user2);
        em.persist(item2);
        em.persist(booking);
        List<Booking> optionalBooking = bookingRepository.findNextBookingForItem_Id(item2.getId());
        Assertions.assertFalse(optionalBooking.isEmpty());
        Assertions.assertEquals(booking, optionalBooking.get(0));
    }

}