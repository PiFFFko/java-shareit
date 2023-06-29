package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ShortItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, ShortItemRequestDto itemRequestDto);

    List<ItemRequestDtoWithItems> getRequests(Long userId);

    List<ItemRequestDtoWithItems> getRequestsByPages(Long userId, Long from, Long size);

    ItemRequestDtoWithItems getRequest(Long userId, Long requestId);
}
