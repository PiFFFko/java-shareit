package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utility.Create;
import ru.practicum.shareit.utility.Update;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    private List<UserDto> getAllUsers() {
        log.info("GET на получение всех пользователей");
        return UserMapper.toListUserDto(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    private UserDto getUser(@PathVariable Long userId) {
        log.info("GET на получение пользователя {}", userId);
        return UserMapper.toUserDto(userService.getUser(userId));
    }

    @PostMapping
    private UserDto createUser(@RequestBody @Validated(Create.class) UserDto userDto) {
        log.info("POST на создание пользователя: {}", userDto);
        return UserMapper.toUserDto(userService.createUser(UserMapper.toUser(userDto)));
    }

    @PatchMapping("/{userId}")
    private UserDto updateUser(@RequestBody @Validated(Update.class) UserDto userDto, @PathVariable Long userId) {
        log.info("PATCH на обновление пользователя с ID {}, данные для обновления: {}", userId, userDto);
        return UserMapper.toUserDto(userService.updateUser(UserMapper.toUser(userDto), userId));
    }

    @DeleteMapping("/{userId}")
    private void deleteUser(@PathVariable Long userId) {
        log.info("DELETE на пользователя с id {}", userId);
        userService.deleteUser(userId);
    }
}
