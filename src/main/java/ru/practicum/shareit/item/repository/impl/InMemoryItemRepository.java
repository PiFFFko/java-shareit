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
public class InMemoryItemRepository implements ItemRepository {

    private Map<Long, Item> itemRepository = new HashMap<>();
    private Long idGenerator = Long.valueOf(1);

    @Override
    public List<Item> getAllUserItems(Long userId) {
        return itemRepository.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getItem(Long itemId) {
        if (itemRepository.containsKey(itemId)) {
            return itemRepository.get(itemId);
        }
        throw new EntityNotExistException(String.format("Вещи с ID не найдено", itemId));
    }

    @Override
    public List<Item> searchItems(String text) {
        String finalText = text.toLowerCase();
        return itemRepository.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(finalText)
                        || item.getDescription().toLowerCase().contains(finalText))
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
                if (item.getName() != null) {
                    itemToUpdate.setName(item.getName().isBlank() ? itemToUpdate.getName() : item.getName());
                }
                if (item.getDescription() != null) {
                    itemToUpdate.setDescription(item.getDescription().isBlank() ? itemToUpdate.getDescription() : item.getDescription());
                }
                itemToUpdate.setAvailable(item.getAvailable() == null ? itemToUpdate.getAvailable() : item.getAvailable());
                return itemToUpdate;
            } else {
                throw new UpdateByNotOwnerException("Изменять данные о вещи может только ее владелец");
            }
        }
        throw new EntityNotExistException(String.format("Вещи с ID %s не найдено", item.getId()));
    }

    @Override
    public void deleteItem(Long itemId) {
        if (itemRepository.remove(itemId) == null) {
            throw new EntityNotExistException(String.format("Вещи с ID %s не найдено", itemId));
        }
    }
}
