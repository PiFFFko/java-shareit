package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.exception.UpdateByNotOwnerException;
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
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotExistException("Предмета не существует"));
        return item;
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
}
