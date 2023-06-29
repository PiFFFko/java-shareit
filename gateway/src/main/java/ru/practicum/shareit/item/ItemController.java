package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.dto.CommentForPostDto;
import ru.practicum.shareit.item.model.dto.ItemDto;

import javax.validation.Valid;
import java.util.Collections;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET на получение всех вещей пользователя с ID: {}", userId);
        return itemClient.getAllUserItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("GET на получение вещи с ID: {}", itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        log.info("GET на поиск по слову: {}", text);
        if (text.isBlank()) {
            return ResponseEntity.ok().body(Collections.emptyList());
        }
        return itemClient.searchItems(text);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("POST на создание вещи {}, владелец {}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody ItemDto itemDto,
                                     @PathVariable Long itemId) {
        log.info("PATCH на обновление вещи с ID {}, пользователем {}, данные для обновления: {}", itemId, userId, itemDto);
        return itemClient.updateItem(userId, itemDto, itemId);
    }

    @DeleteMapping("/itemId")
    public void deleteItem(@PathVariable Long itemId) {
        log.info("DELETE на удаление вещи с ID {}", itemId);
        itemClient.deleteItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long itemId,
                                        @RequestBody @Valid CommentForPostDto commentDto) {
        return itemClient.createComment(userId, itemId, commentDto);

    }
}
