package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemWithBookingsDto> getAllUserItems(Long userID);

    ItemWithBookingsDto getItem(Long userId, Long itemId);

    List<Item> searchItems(String text);

    Item createItem(Long userId, Item item);

    Item updateItem(Long userId, Item item, Long itemId);

    void deleteItem(Long itemId);

    Comment createComment(Long userId, Long itemId, Comment comment);
}
