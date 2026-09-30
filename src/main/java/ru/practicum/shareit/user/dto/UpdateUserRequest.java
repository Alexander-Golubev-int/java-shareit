package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import ru.practicum.shareit.validation.NotBlankIfPresent;

@Data
public class UpdateUserRequest {

    @NotBlankIfPresent
    private String name;
    @Email(message = "Неправильно указан email")
    private String email;
}
