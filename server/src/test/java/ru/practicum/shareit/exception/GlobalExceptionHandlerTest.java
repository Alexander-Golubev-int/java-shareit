package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.ErrorResponse;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlerHttpMessageInternalServerError_ShouldReturnInternalServerError() {
        ResponseEntity<ErrorResponse> response = handler.handlerHttpMessageInternalServerError();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).contains("ошибка");
    }

    @Test
    void handlerHttpMessageValidationException_ShouldReturnConflict() {
        ValidationException ex = new ValidationException("Email уже занят");

        ResponseEntity<ErrorResponse> response = handler.handlerHttpMessageValidationException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getError()).isEqualTo("Email уже занят");
    }

    @Test
    void handleDuplicate_ShouldReturnConflict() {
        DuplicateKeyException ex = new DuplicateKeyException("duplicate key");

        ResponseEntity<ErrorResponse> response = handler.handleDuplicate(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getError()).isEqualTo("duplicate key");
    }

    @Test
    void handleNotFound_ShouldReturnNotFound() {
        NotFoundException ex = new NotFoundException("Не найдено");

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getError()).isEqualTo("Не найдено");
    }

    @Test
    void handleBadRequest_ShouldReturnBadRequest() {
        BadRequestException ex = new BadRequestException("Плохой запрос");

        ResponseEntity<ErrorResponse> response = handler.handleBadRequest(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError()).isEqualTo("Плохой запрос");
    }
}