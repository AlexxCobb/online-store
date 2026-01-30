package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordDto(
        @NotBlank(message = "Текущий пароль обязателен!")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
                 message = "Пароль должен содержать не менее 8 символов: одну заглавную букву, одну строчную букву и одну цифру.")
        String currentPassword,
        @NotBlank(message = "Новый пароль обязателен!")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
                 message = "Пароль должен содержать не менее 8 символов: одну заглавную букву, одну строчную букву и одну цифру.")
        String newPassword
) {
}