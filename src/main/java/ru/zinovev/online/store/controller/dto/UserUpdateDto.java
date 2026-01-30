package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;


public record UserUpdateDto(
        @Size(max = 250, message = "Название должно содержать до {max} символов")
        String name,
        @Size(max = 250, message = "Название должно содержать до {max} символов")
        String lastname,
        @Email(message = "Некорректный формат email")
        @Size(max = 254, message = "Название должно содержать до {max} символов")
        String email
) {
}
