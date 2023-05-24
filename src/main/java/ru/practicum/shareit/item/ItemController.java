package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    private Collection<ItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("GET на получение всех вещей пользователя с ID ", userId);
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/{itemId}")
    private ItemDto getItem(@PathVariable Integer itemId) {
        log.info("GET на получение вещи с ID ", itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        log.info("GET на поиск по слову: ", text);
        return itemService.searchItems(text);
    }

    @PostMapping
    private ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("POST на создание вещи {}, владелец {}", itemDto, userId);
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    private ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                               @RequestBody ItemDto itemDto,
                               @PathVariable Integer itemId) {
        log.info("PATCH на обновление вещи с ID {}, пользователем {}, данные для обновления: {}", itemId, userId, itemDto);
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @DeleteMapping("/itemId")
    private void deleteItem(@PathVariable Integer itemId) {
        log.info("DELETE на удаление вещи с ID {}", itemId);
        itemService.deleteItem(itemId);
    }

}
