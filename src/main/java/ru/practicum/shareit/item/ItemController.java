package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentForPostDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collections;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET на получение всех вещей пользователя с ID: {}", userId);
        return ResponseEntity.ok().body(itemService.getAllUserItems(userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("GET на получение вещи с ID: {}", itemId);
        return ResponseEntity.ok().body(itemService.getItem(userId, itemId));
    }

    @GetMapping("/search")
    public ResponseEntity searchItems(@RequestParam String text) {
        log.info("GET на поиск по слову: {}", text);
        if (text.isBlank()) {
            return ResponseEntity.ok().body(Collections.emptyList());
        }
        return ResponseEntity.ok().body(ItemMapper.toListItemDto(itemService.searchItems(text)));
    }

    @PostMapping
    public ResponseEntity createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("POST на создание вещи {}, владелец {}", itemDto, userId);
        return ResponseEntity.ok().body(itemService.createItem(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody ItemDto itemDto,
                                     @PathVariable Long itemId) {
        log.info("PATCH на обновление вещи с ID {}, пользователем {}, данные для обновления: {}", itemId, userId, itemDto);
        return ResponseEntity.ok().body(itemService.updateItem(userId, itemDto, itemId));
    }

    @DeleteMapping("/itemId")
    public void deleteItem(@PathVariable Long itemId) {
        log.info("DELETE на удаление вещи с ID {}", itemId);
        itemService.deleteItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long itemId,
                                        @RequestBody @Valid CommentForPostDto commentDto) {
        return ResponseEntity.ok().body(CommentMapper.toCommentDto(itemService.createComment(userId, itemId, CommentMapper.toComment(commentDto))));

    }
}
