package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import java.util.Collection;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUser(Long userId);

    User createUser(User user);

    User updateUser(User user, Long userId);

    void deleteUser(Long userId);
}
