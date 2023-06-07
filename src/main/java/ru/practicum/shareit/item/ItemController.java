package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
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
    private List<ItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET на получение всех вещей пользователя с ID: {}", userId);
        return ItemMapper.toListItemDto(itemService.getAllUserItems(userId));
    }

    @GetMapping("/{itemId}")
    private ItemDto getItem(@PathVariable Long itemId) {
        log.info("GET на получение вещи с ID: {}", itemId);
        return ItemMapper.toItemDto(itemService.getItem(itemId));
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
    private ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("POST на создание вещи {}, владелец {}", itemDto, userId);
        return ItemMapper.toItemDto(itemService.createItem(userId, ItemMapper.toItem(itemDto)));
    }

    @PatchMapping("/{itemId}")
    private ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @RequestBody ItemDto itemDto,
                               @PathVariable Long itemId) {
        log.info("PATCH на обновление вещи с ID {}, пользователем {}, данные для обновления: {}", itemId, userId, itemDto);
        return ItemMapper.toItemDto(itemService.updateItem(userId, ItemMapper.toItem(itemDto), itemId));
    }

    @DeleteMapping("/itemId")
    private void deleteItem(@PathVariable Long itemId) {
        log.info("DELETE на удаление вещи с ID {}", itemId);
        itemService.deleteItem(itemId);
    }

}
