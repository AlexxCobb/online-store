package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.zinovev.online.store.controller.validator.ValidBirthDate;

import java.time.LocalDate;

public record UserRegistrationDto(
        @NotBlank
        @Size(min = 1, max = 250)
        String name,
        @NotBlank
        @Size(min = 2, max = 250)
        String lastname,
        @NotBlank
        @ValidBirthDate
        LocalDate birthday,
        @NotBlank
        @Email
        @Size(min = 6, max = 254)
        String email,
        @NotBlank
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
                message = "Password must contain at least 8 characters, one uppercase, one lowercase and one digit"
        )
        String password
) {
}