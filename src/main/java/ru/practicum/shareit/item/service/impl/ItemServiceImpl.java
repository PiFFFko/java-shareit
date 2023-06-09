package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.exception.CommentByNotBookerException;
import ru.practicum.shareit.item.exception.UpdateByNotOwnerException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemWithBookingsDto> getAllUserItems(Long userId) {
        List<Item> items = itemRepository.getAllUserItems(userId);
        List<ItemWithBookingsDto> itemsToGet = new ArrayList<>();
        for (Item item : items) {
            List<Booking> lastBookings = bookingRepository.findLastBookingForItem_Id(item.getId());
            if (!lastBookings.isEmpty()) {
                item.setLastBooking(lastBookings.get(0));
            }
            List<Booking> nextBookings = bookingRepository.findNextBookingForItem_Id(item.getId());
            if (!nextBookings.isEmpty()) {
                item.setNextBooking(nextBookings.get(0));
            }
            List<CommentDto> commentDtos = CommentMapper.toListCommentDto(commentRepository.findAllByItem_Id(item.getId()));
            itemsToGet.add(ItemMapper.toitemWithBookingsAndCommentsDto(item, commentDtos));
        }
        return itemsToGet;
    }

    @Override
    public ItemWithBookingsDto getItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotExistException("Предмета не существует"));
        if (item.getOwner().getId().equals(userId)) {
            List<Booking> lastBookings = bookingRepository.findLastBookingForItem_Id(itemId);
            if (!lastBookings.isEmpty()) {
                item.setLastBooking(lastBookings.get(0));
            }
            List<Booking> nextBookings = bookingRepository.findNextBookingForItem_Id(itemId);
            if (!nextBookings.isEmpty()) {
                item.setNextBooking(nextBookings.get(0));
            }
        }
        List<CommentDto> commentsDto = CommentMapper.toListCommentDto(commentRepository.findAllByItem_Id(itemId));
        return ItemMapper.toitemWithBookingsAndCommentsDto(item, commentsDto);
    }

    @Override
    public List<Item> searchItems(String text) {
        List<Item> items = itemRepository.findAllByNameContainingOrDescriptionContainingIgnoreCase(text);
        return items;
    }

    @Override
    public Item createItem(Long userId, Item item) {
        item.setOwner(userRepository.findById(userId).orElseThrow(() -> new EntityNotExistException("Такого пользователя нет")));
        Item itemToSave = itemRepository.save(item);
        return itemToSave;
    }

    @Override
    public Item updateItem(Long userId, Item item, Long itemId) {
        Item itemToUpdate = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotExistException("Предмета не существует"));
        item.setOwner(userRepository.findById(userId).orElseThrow(() -> new EntityNotExistException("Такого пользователя нет")));
        item.setId(itemId);
        if (item.getOwner().equals(itemToUpdate.getOwner())) {
            if (item.getName() != null && !item.getName().isBlank()) {
                itemToUpdate.setName(item.getName());
            }
            if (item.getDescription() != null && !item.getDescription().isBlank()) {
                itemToUpdate.setDescription(item.getDescription());
            }
            itemToUpdate.setAvailable(item.getAvailable() == null ? itemToUpdate.getAvailable() : item.getAvailable());
            itemRepository.save(itemToUpdate);
            return itemToUpdate;
        } else {
            throw new UpdateByNotOwnerException("Изменять данные о вещи может только ее владелец");
        }
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public Comment createComment(Long userId, Long itemId, Comment comment) {
        List<Booking> bookings = bookingRepository.findAllPastByUserIdAndSortByDesc(userId);
        if (bookings.isEmpty()) {
            throw new CommentByNotBookerException("Пользователь не брал предмет в аренду");
        }
        comment.setAuthor(userRepository.findById(userId).orElseThrow(() -> new EntityNotExistException("Пользователя не существует")));
        comment.setItem(itemRepository.findById(itemId).orElseThrow(() -> new EntityNotExistException("Предмета не существует")));
        comment.setCreated(LocalDateTime.now());
        Comment commentToSave = commentRepository.save(comment);
        return commentToSave;
    }
}
