package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;

import java.util.Collection;

public interface ItemRepository {
    Collection<Item> getAllUserItems(Integer userId);

    Item getItem(Integer itemId);

    Collection<Item> searchItems(String text);

    Item createItem(Item item);

    Item updateItem(Item item);

    void deleteItem(Integer itemId);
}
