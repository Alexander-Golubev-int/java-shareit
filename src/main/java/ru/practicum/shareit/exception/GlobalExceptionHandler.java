package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handlerValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(InternalError.class)
    public ResponseEntity<Map<String, String>> handlerHttpMessageInternalServerError() {
        Map<String, String> errors = new HashMap<>();
        log.error("Внутреняя ошибка сервера.");
        errors.put("error:", "При обработке запроса произошла ошибка");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handlerHttpMessageValidationException(ValidationException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error:", e.getMessage());
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, String>> handleDuplicate(DuplicateKeyException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", e.getMessage());
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NotFoundException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", e.getMessage());
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }
}
