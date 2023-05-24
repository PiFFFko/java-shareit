package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Collection<ItemDto> getAllUserItems(Integer userId) {
        return itemRepository.getAllUserItems(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        return ItemMapper.toItemDto(itemRepository.getItem(itemId));
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        return itemRepository.searchItems(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(Integer userId, ItemDto itemDto) {
        //Если пользователь отсутствует, то выкинется исключение
        userRepository.getUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        return ItemMapper.toItemDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto updateItem(Integer userId, ItemDto itemDto, Integer itemId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        item.setId(itemId);
        return ItemMapper.toItemDto(itemRepository.updateItem(item));
    }

    @Override
    public void deleteItem(Integer itemId) {
        itemRepository.deleteItem(itemId);
    }
}
