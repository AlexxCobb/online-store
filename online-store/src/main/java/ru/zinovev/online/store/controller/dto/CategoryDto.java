package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryDto(
        @NotBlank(message = "Category name required")
        @Size(min = 5, max = 100, message = "Category name must be between 5 and 100 characters")
        String name
) {
}
