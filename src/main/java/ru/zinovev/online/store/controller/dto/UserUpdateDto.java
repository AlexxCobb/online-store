package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import ru.zinovev.online.store.controller.validator.NullOrNotBlank;


public record UserUpdateDto(
        @Size(max = 250, message = "Название должно содержать до {max} символов")
        @NullOrNotBlank
        String name,
        @Size(max = 250, message = "Название должно содержать до {max} символов")
        @NullOrNotBlank
        String lastname,
        @NullOrNotBlank
        @Email(message = "Некорректный формат email")
        @Size(max = 254, message = "Название должно содержать до {max} символов")
        String email
) {
}
