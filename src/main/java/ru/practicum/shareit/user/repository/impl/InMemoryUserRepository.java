package ru.practicum.shareit.user.repository.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityAlreadyExistException;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private Map<Long, User> userRepository = new HashMap();
    private Long idGenerator = Long.valueOf(1);

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.values());
    }

    @Override
    public User getUser(Long userId) {
        if (userRepository.containsKey(userId))
            return userRepository.get(userId);
        throw new EntityNotExistException(String.format("Пользователься с ID %s не найдено", userId));
    }

    @Override
    public User createUser(User user) {
        if (isEmailAlreadyExist(user))
            throw new EntityAlreadyExistException("Пользователь с таким Email уже существует");
        user.setId(idGenerator++);
        userRepository.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (userRepository.containsKey(user.getId())) {
            if (isEmailAlreadyExist(user))
                throw new EntityAlreadyExistException("Пользователь с таким Email уже существует");
            User userToUpdate = userRepository.get(user.getId());
            if (user.getName() != null) {
                userToUpdate.setName(user.getName().isBlank() ? userToUpdate.getName() : user.getName());
            }
            userToUpdate.setEmail(user.getEmail() == null ? userToUpdate.getEmail() : user.getEmail());
            return userToUpdate;
        }
        throw new EntityNotExistException(String.format("Пользователься с ID %s не найдено", user.getId()));
    }

    @Override
    public void deleteUser(Long userId) {
        if (userRepository.containsKey(userId)) {
            userRepository.remove(userId);
        } else
            throw new EntityNotExistException(String.format("Пользователься с ID %s не найдено", userId));
    }

    private Boolean isEmailAlreadyExist(User user) {
        return userRepository.values().stream()
                .filter(user1 -> !user1.getId().equals(user.getId()))
                .anyMatch(user1 -> user1.getEmail().equals(user.getEmail()));

    }
}
