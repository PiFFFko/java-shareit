package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    private Collection<UserDto> getAllUsers() {
        log.info("GET на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    private UserDto getUser(@PathVariable Integer userId) {
        log.info("GET на получение пользователя {}", userId);
        return userService.getUser(userId);
    }

    @PostMapping
    private UserDto createUser(@RequestBody @Valid UserDto userDto) {
        log.info("POST на создание пользователя: {}", userDto);
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    private UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Integer userId) {
        log.info("PATCH на обновление пользователя с ID {}, данные для обновления: {}", userId, userDto);
        return userService.updateUser(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    private void deleteUser(@PathVariable Integer userId) {
        log.info("DELETE на пользователя с id {}", userId);
        userService.deleteUser(userId);
    }
}
