package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemWithBookingsDto> getAllUserItems(Long userID);

    ItemWithBookingsDto getItem(Long userId, Long itemId);

    List<Item> searchItems(String text);

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);

    void deleteItem(Long itemId);

    Comment createComment(Long userId, Long itemId, Comment comment);
}
