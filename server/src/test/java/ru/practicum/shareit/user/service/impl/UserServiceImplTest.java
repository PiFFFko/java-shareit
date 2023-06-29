package ru.practicum.shareit.user.service.impl;


import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private final EasyRandom generator = new EasyRandom();
    @Mock
    UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    public void beforeEach() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void updateUser() {
        User user = generator.nextObject(User.class);
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        User userToUpdate = userService.updateUser(user, user.getId());
        assertEquals(user.getId(), userToUpdate.getId());
        assertEquals(user.getName(), userToUpdate.getName());
        assertEquals(user.getEmail(), userToUpdate.getEmail());
        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    void updateUserOnlyName() {
        User user = generator.nextObject(User.class);
        user.setEmail(null);
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        User userToUpdate = userService.updateUser(user, user.getId());
        assertEquals(user.getId(), userToUpdate.getId());
        assertEquals(user.getName(), userToUpdate.getName());
        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    void updateUserOnlyEmail() {
        User user = generator.nextObject(User.class);
        user.setName(null);
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        User userToUpdate = userService.updateUser(user, user.getId());
        assertEquals(user.getId(), userToUpdate.getId());
        assertEquals(user.getName(), userToUpdate.getName());
        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    void updateUserWhichNotExist() {
        User user = generator.nextObject(User.class);
        when(userRepository.findById(Mockito.anyLong()))
                .thenThrow(EntityNotExistException.class);
        assertThrows(EntityNotExistException.class, () -> userService.updateUser(user, user.getId()));
        verify(userRepository, never()).save(Mockito.any(User.class));
    }

}