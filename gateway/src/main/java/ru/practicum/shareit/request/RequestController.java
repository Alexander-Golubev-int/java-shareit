package ru.practicum.shareit.request;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constans.Constants;
import ru.practicum.shareit.request.dto.RequestRequestDto;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID должен быть " +
                                                        "больше 0") Long userId,
                                                @Validated @RequestBody RequestRequestDto newItemRequest) {
        return requestClient.createRequest(userId, newItemRequest);
    }


    @GetMapping
    public ResponseEntity<Object> getItemRequest(@RequestHeader(Constants.USER_ID_HEADER) @Validated @Positive(message = "ID должен быть " +
            "больше 0") Long userId) {
        return requestClient.getRequest(userId);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Object> getItemRequest() {
        return requestClient.getAllRequest();
    }

    @GetMapping(path = "/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable @Validated @Positive(message = "ID должен быть " +
            "больше 0") Long requestId) {
        return requestClient.getItemRequestById(requestId);
    }

}
