package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ShortItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String SHARER_USER_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(@RequestHeader(name = SHARER_USER_HEADER) Long userId,
                                                        @RequestBody @Valid ShortItemRequestDto itemRequestDto) {
        return ResponseEntity.ok(itemRequestService.createItemRequest(userId, itemRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDtoWithItems>> getRequests(@RequestHeader(name = SHARER_USER_HEADER) Long userId) {
        return ResponseEntity.ok(itemRequestService.getRequests(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDtoWithItems>> getRequestsByPages(@RequestHeader(name = SHARER_USER_HEADER) Long userId,
                                             @RequestParam(defaultValue = "0") Long from,
                                             @RequestParam(defaultValue = "5") Long size) {
        if (from < 0) {
            throw new InvalidParameterException("Индекс первого элемента не может быть меньше 0");
        }
        if (size <= 0) {
            throw new InvalidParameterException("Количество элементов для отображения должно быть больше 0");
        }
        return ResponseEntity.ok(itemRequestService.getRequestsByPages(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDtoWithItems> getRequest(@RequestHeader(name = SHARER_USER_HEADER) Long userId,
                                     @PathVariable Long requestId) {
        return ResponseEntity.ok(itemRequestService.getRequest(userId, requestId));
    }

}
