package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryDto(
        @NotBlank(message = "Укажите название категории")
        @Size(min = 5, max = 100, message = "Название должно содержать от {min} до {max} символов")
        String name
) {
}