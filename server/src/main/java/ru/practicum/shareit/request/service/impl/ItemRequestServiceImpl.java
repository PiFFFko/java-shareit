package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ShortItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createItemRequest(Long userId, ShortItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(userRepository.findById(userId).orElseThrow(() -> new EntityNotExistException("Пользователь не найден")));
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDtoWithItems> getRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotExistException("Пользователя не существует"));
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestor_Id(userId);
        List<ItemRequestDtoWithItems> itemRequestsDto = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemRepository.findItemsByRequest(itemRequest.getId());
            ItemRequestDtoWithItems itemRequestDto = ItemRequestMapper.toItemRequestDtoWithItems(itemRequest, items);
            itemRequestsDto.add(itemRequestDto);
        }
        return itemRequestsDto;
    }

    @Override
    public List<ItemRequestDtoWithItems> getRequestsByPages(Long userId, Long from, Long size) {
        List<ItemRequestDtoWithItems> itemRequestsDto = new ArrayList<>();
        Sort sortByCreated = Sort.by("created").ascending();
        from = from > 0 ? from / size : 0;
        Pageable page = PageRequest.of(from.intValue(), size.intValue(), sortByCreated);
        do {
            Page<ItemRequest> itemsRequestsPage = itemRequestRepository.findByRequestor_IdNot(userId, page);
            itemsRequestsPage.getContent().forEach(itemRequest -> {
                List<Item> items = itemRepository.findItemsByRequest(itemRequest.getId());
                ItemRequestDtoWithItems itemRequestDto = ItemRequestMapper.toItemRequestDtoWithItems(itemRequest, items);
                itemRequestsDto.add(itemRequestDto);
            });
            if (itemsRequestsPage.hasNext()) {
                page = PageRequest.of(itemsRequestsPage.getNumber() + 1, itemsRequestsPage.getSize(), itemsRequestsPage.getSort());
            } else {
                page = null;
            }
        } while (page != null);
        return itemRequestsDto;
    }

    @Override
    public ItemRequestDtoWithItems getRequest(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotExistException("Пользователя не существует"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new EntityNotExistException("Запроса не существует"));
        List<Item> items = itemRepository.findItemsByRequest(itemRequest.getId());
        ItemRequestDtoWithItems itemRequestDto = ItemRequestMapper.toItemRequestDtoWithItems(itemRequest, items);
        return itemRequestDto;
    }
}
