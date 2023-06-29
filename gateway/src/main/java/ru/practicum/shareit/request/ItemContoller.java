package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.ShortItemRequestDto;

import javax.validation.Valid;
import java.security.InvalidParameterException;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemContoller {

    private static final String SHARER_USER_HEADER = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(name = SHARER_USER_HEADER) Long userId,
                                        @RequestBody @Valid ShortItemRequestDto itemRequestDto) {
        return itemRequestClient.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(name = SHARER_USER_HEADER) Long userId) {
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsByPages(@RequestHeader(name = SHARER_USER_HEADER) Long userId,
                                             @RequestParam(defaultValue = "0") Long from,
                                             @RequestParam(defaultValue = "5") Long size) {
        if (from < 0) {
            throw new InvalidParameterException("Индекс первого элемента не может быть меньше нуля");
        }
        if (size < 0 || size == 0) {
            throw new InvalidParameterException("Количество элементов для отображения не может быть меньше или равно нулю");
        }
        return itemRequestClient.getRequestsByPages(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(name = SHARER_USER_HEADER) Long userId,
                                     @PathVariable Long requestId) {
        return itemRequestClient.getRequest(userId, requestId);
    }
}
