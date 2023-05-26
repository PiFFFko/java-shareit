package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getAllUserItems(Long userId);

    Item getItem(Long itemId);

    List<Item> searchItems(String text);

    Item createItem(Item item);

    Item updateItem(Item item);

    void deleteItem(Long itemId);
}
