package ru.practicum.shareit.request.service.impl;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ShortItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ItemRequestServiceImplIntegrationTest {

    private final EasyRandom generator = new EasyRandom();
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User user;

    @BeforeEach
    void beforeEach() {
        user = generator.nextObject(User.class);
    }

    @Test
    @DirtiesContext
    void createItemRequest() {
        User userToSave = userRepository.save(user);
        ShortItemRequestDto shortItemRequestDto = generator.nextObject(ShortItemRequestDto.class);
        ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(userToSave.getId(), shortItemRequestDto);
        assertEquals(shortItemRequestDto.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    @DirtiesContext
    void createItemRequestByNotExistingUser() {
        ShortItemRequestDto shortItemRequestDto = generator.nextObject(ShortItemRequestDto.class);
        assertThrows(EntityNotExistException.class, () -> itemRequestService.createItemRequest(generator.nextLong(), shortItemRequestDto));
    }

    @Test
    @DirtiesContext
    void getRequestsByNotExistingUser() {
        assertThrows(EntityNotExistException.class, () -> itemRequestService.getRequests(generator.nextLong()));
    }

    @Test
    @DirtiesContext
    void getRequests() {
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);
        User userToSave = userRepository.save(user);
        itemRequest.setRequestor(userToSave);
        itemRequestRepository.save(itemRequest);
        List<ItemRequestDtoWithItems> itemRequestList = itemRequestService.getRequests(userToSave.getId());
        assertEquals(itemRequestList.get(0).getDescription(), itemRequest.getDescription());
    }

}