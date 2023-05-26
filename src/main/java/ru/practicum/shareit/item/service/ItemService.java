package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    List<Item> getAllUserItems(Long userID);

    Item getItem(Long itemId);

    List<Item> searchItems(String text);

    Item createItem(Long userId, Item item);

    Item updateItem(Long userId, Item item, Long itemId);

    void deleteItem(Long itemId);
}
