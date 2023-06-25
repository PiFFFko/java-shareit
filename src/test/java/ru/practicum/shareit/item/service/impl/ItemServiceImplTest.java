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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.exception.CommentByNotBookerException;
import ru.practicum.shareit.item.exception.UpdateByNotOwnerException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
    User user;
    ItemRequest itemRequest;

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
        user = generator.nextObject(User.class);
        booking = generator.nextObject(Booking.class);
        booking2 = generator.nextObject(Booking.class);
        comment = generator.nextObject(Comment.class);
        itemRequest = generator.nextObject(ItemRequest.class);
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
    void searchItem() {
        Mockito.when(itemRepository.findAllByNameContainingOrDescriptionContainingIgnoreCase(Mockito.anyString()))
                .thenReturn(List.of(item));
        List<Item> items = itemService.searchItems(generator.nextObject(String.class));
        assertEquals(items.get(0).getName(), item.getName());
        Mockito.verify(itemRepository, Mockito.times(1)).findAllByNameContainingOrDescriptionContainingIgnoreCase(Mockito.anyString());
    }

    @Test
    void searchItemButNotFound() {
        Mockito.when(itemRepository.findAllByNameContainingOrDescriptionContainingIgnoreCase(Mockito.anyString()))
                .thenReturn(Collections.emptyList());
        List<Item> items = itemService.searchItems(generator.nextObject(String.class));
        assertEquals(items.size(), 0);
        Mockito.verify(itemRepository, Mockito.times(1)).findAllByNameContainingOrDescriptionContainingIgnoreCase(Mockito.anyString());
    }

    @Test
    void getNotExistingItem() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenThrow(EntityNotExistException.class);
        assertThrows(EntityNotExistException.class, () -> itemService.getItem(1L, 1L));
    }

    @Test
    void createItemByNotExistingUser() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotExistException.class, () -> itemService.createItem(generator.nextLong(), itemDto));
    }

    @Test
    void createItem() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocationOnMock -> {
                    item.setId(1L);
                    return item;
                });
        ItemDto itemDtoSaved = itemService.createItem(generator.nextLong(), itemDto);
        assertNotEquals(itemDto.getId(), itemDtoSaved.getId());
    }


    @Test
    void createItemWhenNoRequest() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setRequestId(null);
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocationOnMock -> {
                    item.setId(1L);
                    return item;
                });
        ItemDto itemDtoSaved = itemService.createItem(generator.nextLong(), itemDto);
        assertNotEquals(itemDto.getId(), itemDtoSaved.getId());
        Mockito.verify(itemRequestRepository, Mockito.never()).findById(Mockito.anyLong());
    }

    @Test
    void updateItem() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        Item itemToUpdate = generator.nextObject(Item.class);
        itemToUpdate.setOwner(user);
        assertNotEquals(itemDto.getName(), itemToUpdate.getName());
        assertNotEquals(itemDto.getAvailable(), itemToUpdate.getAvailable());
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemToUpdate));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(itemToUpdate);
        ItemDto itemDtoUpdated = itemService.updateItem(user.getId(), itemDto, generator.nextLong());
        assertEquals(itemDto.getName(), itemDtoUpdated.getName());
        assertEquals(itemDto.getAvailable(), itemDtoUpdated.getAvailable());
    }

    @Test
    void updateItemWhenItemNotExist() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotExistException.class, () -> itemService.updateItem(1L, itemDto, 1L));
    }

    @Test
    void updateItemWhenUserNotExist() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        Item itemToUpdate = generator.nextObject(Item.class);
        itemToUpdate.setOwner(user);
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemToUpdate));
        assertThrows(EntityNotExistException.class, () -> itemService.updateItem(1L, itemDto, 1L));
    }

    @Test
    void updateItemByNotOwner() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        Item itemToUpdate = generator.nextObject(Item.class);
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemToUpdate));
        assertThrows(UpdateByNotOwnerException.class, () -> itemService.updateItem(1L, itemDto, 1L));
    }

    @Test
    void createComment() {
        Mockito.when(bookingRepository.findAllPastByUserIdAndSortByDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(booking, booking2));
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(comment);
        Comment commentToSave = itemService.createComment(user.getId(), item.getId(), comment);
        assertEquals(comment.getText(), commentToSave.getText());
        assertEquals(user, commentToSave.getAuthor());
        assertEquals(item, commentToSave.getItem());
    }

    @Test
    void createCommentWhenUserHaveNoBookings() {
        Mockito.when(bookingRepository.findAllPastByUserIdAndSortByDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(Collections.emptyList());
        assertThrows(CommentByNotBookerException.class, () -> itemService.createComment(1L, 1L, comment));
    }

    @Test
    void createCommentWhenNoUser() {
        Mockito.when(bookingRepository.findAllPastByUserIdAndSortByDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(booking, booking2));
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotExistException.class, () -> itemService.createComment(1L, 1L, comment));
    }

    @Test
    void createCommentWhenNoItem() {
        Mockito.when(bookingRepository.findAllPastByUserIdAndSortByDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(booking, booking2));
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotExistException.class, () -> itemService.createComment(1L, 1L, comment));
    }
}