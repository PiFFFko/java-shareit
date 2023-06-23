package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ShortItemRequestDto;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequest toItemRequest(ShortItemRequestDto itemRequestDto) {
        return new ItemRequest(itemRequestDto.getDescription());
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated().toString());
    }

    public ItemRequestDtoWithItems toItemRequestDtoWithItems(ItemRequest itemRequest, List<Item> items) {
        return new ItemRequestDtoWithItems(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated().toString(),
                items.stream().map(ItemMapper::toItemDtoWithRequest).collect(Collectors.toList()));
    }
}
