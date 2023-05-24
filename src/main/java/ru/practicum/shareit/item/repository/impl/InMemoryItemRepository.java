package ru.practicum.shareit.item.repository.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.exception.UpdateByNotOwnerException;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private Map<Integer, Item> itemRepository = new HashMap<>();
    private Integer idGenerator = 1;

    @Override
    public Collection<Item> getAllUserItems(Integer userId) {
        return itemRepository.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getItem(Integer itemId) {
        if (itemRepository.containsKey(itemId)) {
            return itemRepository.get(itemId);
        }
        throw new EntityNotExistException(String.format("Вещи с ID не найдено", itemId));
    }

    @Override
    public Collection<Item> searchItems(String text) {
        if (text.equals(""))
            return Collections.emptyList();
        return itemRepository.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Item createItem(Item item) {
        item.setId(idGenerator++);
        itemRepository.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        if (itemRepository.containsKey(item.getId())) {
            Item itemToUpdate = itemRepository.get(item.getId());
            if (item.getOwner().equals(itemToUpdate.getOwner())) {
                itemToUpdate.setName(item.getName() == null ? itemToUpdate.getName() : item.getName());
                itemToUpdate.setDescription(item.getDescription() == null ? itemToUpdate.getDescription() : item.getDescription());
                itemToUpdate.setAvailable(item.getAvailable() == null ? itemToUpdate.getAvailable() : item.getAvailable());
                itemRepository.put(itemToUpdate.getId(), itemToUpdate);
                return itemToUpdate;
            } else {
                throw new UpdateByNotOwnerException("Изменять данные о вещи может только ее владелец");
            }
        }
        throw new EntityNotExistException(String.format("Вещи с ID %s не найдено", item.getId()));
    }

    @Override
    public void deleteItem(Integer itemId) {
        if (itemRepository.containsKey(itemId)) {
            itemRepository.remove(itemId);
        } else
            throw new EntityNotExistException(String.format("Вещи с ID %s не найдено", itemId));
    }
}
