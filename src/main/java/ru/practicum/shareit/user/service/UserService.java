package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAllUsers();

    UserDto getUser(Integer userId);

    UserDto createUser(UserDto user);

    UserDto updateUser(UserDto user, Integer userId);

    void deleteUser(Integer userId);
}
