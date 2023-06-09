package ru.practicum.shareit.request.service.impl;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ShortItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {


    private final EasyRandom generator = new EasyRandom();
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    private ItemRequestService itemRequestService;
    private User user;

    @BeforeEach
    public void beforeEach() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRepository, userRepository);
        user = generator.nextObject(User.class);
    }

    @Test
    void createRequestWhenUserNotExists() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenThrow(EntityNotExistException.class);
        Assertions.assertThrows(EntityNotExistException.class,
                () -> itemRequestService.createItemRequest(generator.nextLong(), generator.nextObject(ShortItemRequestDto.class)));
        Mockito.verify(itemRequestRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void createRequest() {
        ShortItemRequestDto shortItemRequestDto = generator.nextObject(ShortItemRequestDto.class);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.save(Mockito.any())).thenReturn(ItemRequestMapper.toItemRequest(shortItemRequestDto));
        Assertions.assertEquals(shortItemRequestDto.getDescription(), itemRequestService.createItemRequest(user.getId(), shortItemRequestDto)
                .getDescription());
        Mockito.verify(itemRequestRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void getRequestsByNotExistingUser() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenThrow(EntityNotExistException.class);
        Assertions.assertThrows(EntityNotExistException.class,
                () -> itemRequestService.getRequests(generator.nextLong()));
        Mockito.verify(itemRequestRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void getRequests() {
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);
        Item item = generator.nextObject(Item.class);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findByRequestor_Id(Mockito.anyLong())).thenReturn(List.of(itemRequest));
        Mockito.when(itemRepository.findItemsByRequest(Mockito.any())).thenReturn(List.of(item));
        Assertions.assertEquals(itemRequest.getDescription(),
                itemRequestService.getRequests(user.getId()).get(0).getDescription());
    }

    @Test
    void getRequestByNotExistingUser() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenThrow(EntityNotExistException.class);
        Assertions.assertThrows(EntityNotExistException.class,
                () -> itemRequestService.getRequest(generator.nextLong(), generator.nextLong()));
        Mockito.verify(itemRequestRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void getNotExistingRequest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenThrow(EntityNotExistException.class);
        Assertions.assertThrows(EntityNotExistException.class,
                () -> itemRequestService.getRequest(generator.nextLong(), generator.nextLong()));
        Mockito.verify(itemRequestRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void getRequest() {
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest));
        Assertions.assertEquals(itemRequest.getDescription(),
                itemRequestService.getRequest(generator.nextLong(), generator.nextLong()).getDescription());
    }

    @Test
    void getRequestWhenNoUser() {
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotExistException.class, () ->
                itemRequestService.getRequest(1L, 1L));
    }

    @Test
    void getRequestWhenNoItemRequest() {
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotExistException.class, () ->
                itemRequestService.getRequest(1L, 1L));
    }

    @Test
    void getRequestByPages() {
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);
        Item item = generator.nextObject(Item.class);
        Mockito.when(itemRequestRepository.findByRequestor_IdNot(Mockito.anyLong(), Mockito.any()))
                .thenAnswer(invocationOnMock -> {
                    List<ItemRequest> itemRequests = List.of(itemRequest);
                    Page<ItemRequest> page = new PageImpl<>(itemRequests);
                    return page;
                });
        Mockito.when(itemRepository.findItemsByRequest(Mockito.anyLong()))
                .thenReturn(List.of(item));
        List<ItemRequestDtoWithItems> itemRequestDtoWithItems = itemRequestService.getRequestsByPages(1L, 5L, 5L);
        Assertions.assertEquals(item.getName(),itemRequestDtoWithItems.get(0).getItems().get(0).getName());
    }

}