package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentForPostDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemWithBookingsDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET на получение всех вещей пользователя с ID: {}", userId);
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("GET на получение вещи с ID: {}", itemId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        log.info("GET на поиск по слову: {}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return ItemMapper.toListItemDto(itemService.searchItems(text));
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("POST на создание вещи {}, владелец {}", itemDto, userId);
        return ItemMapper.toItemDto(itemService.createItem(userId, ItemMapper.toItem(itemDto)));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @RequestBody ItemDto itemDto,
                               @PathVariable Long itemId) {
        log.info("PATCH на обновление вещи с ID {}, пользователем {}, данные для обновления: {}", itemId, userId, itemDto);
        return ItemMapper.toItemDto(itemService.updateItem(userId, ItemMapper.toItem(itemDto), itemId));
    }

    @DeleteMapping("/itemId")
    public void deleteItem(@PathVariable Long itemId) {
        log.info("DELETE на удаление вещи с ID {}", itemId);
        itemService.deleteItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long itemId,
                                     @RequestBody @Valid CommentForPostDto commmentDto) {
        return CommentMapper.toCommentDto(itemService.createComment(userId, itemId, CommentMapper.toComment(commmentDto)));

    }
}
