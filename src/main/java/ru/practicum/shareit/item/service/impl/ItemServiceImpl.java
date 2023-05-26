package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<Item> getAllUserItems(Long userId) {
        return itemRepository.getAllUserItems(userId);
    }

    @Override
    public Item getItem(Long itemId) {
        return itemRepository.getItem(itemId);
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemRepository.searchItems(text);
    }

    @Override
    public Item createItem(Long userId, Item item) {
        //Если пользователь отсутствует, то выкинется исключение
        userRepository.getUser(userId);
        item.setOwner(userId);
        return itemRepository.createItem(item);
    }

    @Override
    public Item updateItem(Long userId, Item item, Long itemId) {
        item.setOwner(userId);
        item.setId(itemId);
        return itemRepository.updateItem(item);
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteItem(itemId);
    }
}
