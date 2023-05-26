package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository {
    List<User> getAllUsers();

    User getUser(Long userId);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long userId);
}
