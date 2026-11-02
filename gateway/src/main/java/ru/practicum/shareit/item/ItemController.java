package ru.practicum.shareit.item;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constans.Constants;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping(path = "/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID должен быть " +
            "больше 0") Long userId, @PathVariable Long itemId) {
        return itemClient.getItemByID(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID должен быть больше 0") Long userId,
                                             @Validated @RequestBody ItemRequestDto itemRequest) {
        return itemClient.createItem(userId, itemRequest);
    }

    @PostMapping(path = "{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID " +
                                                        "должен " +
                                                        "быть больше 0") Long userId, @PathVariable Long itemId,
                                                @Validated @RequestBody NewCommentRequest newCommentRequest) {
        return itemClient.createComment(userId, itemId, newCommentRequest);
    }

    @PatchMapping(path = "/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID должен быть " +
                                                     "больше 0") Long userId, @PathVariable Long itemId,
                                             @Validated @RequestBody UpdateItemRequest updateItemRequest) {
        return itemClient.updateItem(userId, itemId, updateItemRequest);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID " +
            "должен быть больше 0") Long userId) {
        return itemClient.getAllItems(userId);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<Object> getAllItemsBySearch(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message =
            "ID должен быть больше 0") Long userId, @RequestParam String text) {
        return itemClient.getAllItemsBySearch(userId, text);
    }
}
