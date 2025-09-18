package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginDto(
        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный формат email")
        String email,
        @NotBlank(message = "Пароль обязателен")
        String password
) {
}
