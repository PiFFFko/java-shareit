package ru.practicum.shareit.user.service.impl;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplIntegrationTest {

    private final EasyRandom generator = new EasyRandom();
    @Autowired
    UserService userService;

    @Test
    @DirtiesContext
    void createUser() {
        User user = generator.nextObject(User.class);
        user.setId(null);
        User createdUser = userService.createUser(user);
        assertNotNull(createdUser.getId());
    }

    @Test
    @DirtiesContext
    void getUser() {
        User user = generator.nextObject(User.class);
        User createdUser = userService.createUser(user);
        User getUser = userService.getUser(createdUser.getId());
        assertEquals(createdUser, getUser);
    }

    @Test
    @DirtiesContext
    void getUserFailCauseNoUser() {
        assertThrows(EntityNotExistException.class, () -> userService.getUser(generator.nextLong()));
    }


}