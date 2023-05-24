package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Integer userId) {
        return UserMapper.toUserDto(userRepository.getUser(userId));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.createUser(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer userId) {
        User user = UserMapper.toUser(userDto);
        user.setId(userId);
        return UserMapper.toUserDto(userRepository.updateUser(user));
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteUser(userId);
    }
}
