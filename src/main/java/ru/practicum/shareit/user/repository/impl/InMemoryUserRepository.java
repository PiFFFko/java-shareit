package ru.practicum.shareit.user.repository.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityAlreadyExistException;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private Map<Integer, User> userRepository = new HashMap();
    private Integer idGenerator = 1;

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.values();
    }

    @Override
    public User getUser(Integer userId) {
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
            user.setId(user.getId());
            if (isEmailAlreadyExist(user))
                throw new EntityAlreadyExistException("Пользователь с таким Email уже существует");
            User userToUpdate = userRepository.get(user.getId());
            userToUpdate.setName(user.getName() == null ? userToUpdate.getName() : user.getName());
            userToUpdate.setEmail(user.getEmail() == null ? userToUpdate.getEmail() : user.getEmail());
            userRepository.put(userToUpdate.getId(), userToUpdate);
            return userToUpdate;
        }
        throw new EntityNotExistException(String.format("Пользователься с ID %s не найдено", user.getId()));
    }

    @Override
    public void deleteUser(Integer userId) {
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
