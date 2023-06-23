package ru.practicum.shareit.item.service.impl;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private final EasyRandom generator = new EasyRandom();
    ItemService itemService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    Item item;
    Item item2;
    Booking booking;
    Booking booking2;
    Comment comment;

    @BeforeEach
    void beforeEach() {
        itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                itemRequestRepository,
                bookingRepository,
                commentRepository);
        item = generator.nextObject(Item.class);
        item2 = generator.nextObject(Item.class);
        booking = generator.nextObject(Booking.class);
        booking2 = generator.nextObject(Booking.class);
        comment = generator.nextObject(Comment.class);
    }


    @Test
    void getAllUsersItems() {
        Mockito.when(itemRepository.getAllUserItems(Mockito.anyLong())).thenReturn(List.of(item,
                item2));
        Mockito.when(bookingRepository.findLastBookingForItem_Id(Mockito.anyLong())).thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findNextBookingForItem_Id(Mockito.anyLong())).thenReturn(List.of(booking2));
        Mockito.when(commentRepository.findAllByItem_Id(Mockito.anyLong())).thenReturn(List.of(comment));
        List<ItemWithBookingsDto> items = itemService.getAllUserItems(1L);
        assertEquals(items.size(), 2);
        assertEquals(items.get(0).getDescription(), item.getDescription());
        assertEquals(items.get(1).getDescription(), item2.getDescription());
        assertEquals(items.get(0).getLastBooking().getBookerId(), booking.getBooker().getId());
        assertEquals(items.get(0).getNextBooking().getBookerId(), booking2.getBooker().getId());
    }

    @Test
    void getItem() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findLastBookingForItem_Id(Mockito.anyLong())).thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findNextBookingForItem_Id(Mockito.anyLong())).thenReturn(List.of(booking2));
        Mockito.when(commentRepository.findAllByItem_Id(Mockito.anyLong())).thenReturn(List.of(comment));
        ItemWithBookingsDto itemToGet = itemService.getItem(item.getOwner().getId(), item.getId());
        assertEquals(itemToGet.getDescription(), item.getDescription());
        assertEquals(itemToGet.getLastBooking().getBookerId(), booking.getBooker().getId());
        assertEquals(itemToGet.getNextBooking().getBookerId(), booking2.getBooker().getId());
    }

    @Test
    void getNotExistingItem() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenThrow(EntityNotExistException.class);
        assertThrows(EntityNotExistException.class, () -> itemService.getItem(1L, 1L));
    }


}