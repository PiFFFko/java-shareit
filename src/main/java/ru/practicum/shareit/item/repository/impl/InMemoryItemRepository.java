package ru.practicum.shareit.item.repository.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.exception.UpdateByNotOwnerException;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository {

    private Map<Long, Item> itemRepository = new HashMap<>();
    private Long idGenerator = Long.valueOf(1);


    public List<Item> getAllUserItems(Long userId) {
        return itemRepository.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
    }


    public Item getItem(Long itemId) {
        Item item = itemRepository.get(itemId);
        if (item != null) {
            return item;
        }
        throw new EntityNotExistException(String.format("Вещи с ID не найдено", itemId));
    }


    public List<Item> searchItems(String text) {
        String finalText = text.toLowerCase();
        return itemRepository.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(finalText)
                        || item.getDescription().toLowerCase().contains(finalText))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }


    public Item createItem(Item item) {
        item.setId(idGenerator++);
        itemRepository.put(item.getId(), item);
        return item;
    }


    public Item updateItem(Item item) {
        if (itemRepository.containsKey(item.getId())) {
            Item itemToUpdate = itemRepository.get(item.getId());
            if (item.getOwner().equals(itemToUpdate.getOwner())) {
                if (item.getName() != null && !item.getName().isBlank()) {
                    itemToUpdate.setName(item.getName());
                }
                if (item.getDescription() != null && !item.getDescription().isBlank()) {
                    itemToUpdate.setDescription(item.getDescription());
                }
                itemToUpdate.setAvailable(item.getAvailable() == null ? itemToUpdate.getAvailable() : item.getAvailable());
                return itemToUpdate;
            } else {
                throw new UpdateByNotOwnerException("Изменять данные о вещи может только ее владелец");
            }
        }
        throw new EntityNotExistException(String.format("Вещи с ID %s не найдено", item.getId()));
    }


    public void deleteItem(Long itemId) {
        if (itemRepository.remove(itemId) == null) {
            throw new EntityNotExistException(String.format("Вещи с ID %s не найдено", itemId));
        }
    }
}
