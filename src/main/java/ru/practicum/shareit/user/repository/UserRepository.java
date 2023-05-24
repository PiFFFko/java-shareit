package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserRepository {
    Collection<User> getAllUsers();

    User getUser(Integer userId);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Integer userId);
}
