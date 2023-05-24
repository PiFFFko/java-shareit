package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> getAllUserItems(Integer userID);

    ItemDto getItem(Integer itemId);

    Collection<ItemDto> searchItems(String text);

    ItemDto createItem(Integer userId, ItemDto itemDto);

    ItemDto updateItem(Integer userId, ItemDto itemDto, Integer itemId);

    void deleteItem(Integer itemId);
}
