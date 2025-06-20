package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Optional;

public record UserUpdateDto(
        @Size(min = 1, max = 250)
        Optional<String> name,
        @Size(min = 2, max = 250)
        Optional<String> lastname,
        @Email
        @Size(min = 6, max = 254)
        Optional<String> email
) {
}
