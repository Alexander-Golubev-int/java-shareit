package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewUserRequest {
    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Неправильно указан email")
    @NotBlank(message = "Email не должен быть пустым")
    private String email;

}
