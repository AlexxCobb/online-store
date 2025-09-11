package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordDto(
        @NotBlank
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
                 message = "Password must contain at least 8 characters, one uppercase, one lowercase and one digit")
        String currentPassword,
        @NotBlank
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
                 message = "Password must contain at least 8 characters, one uppercase, one lowercase and one digit")
        String newPassword
) {
}
