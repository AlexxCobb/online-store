package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Optional;

public record UserUpdateDto(
        @Size(min = 1, max = 250)
        String name,
        @Size(min = 2, max = 250)
        String lastname,
        @Email
        @Size(min = 6, max = 254)
        String email
) {

    public Optional<String> getOptionalName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getOptionalLastname() {
        return Optional.ofNullable(lastname);
    }

    public Optional<String> getOptionalEmail() {
        return Optional.ofNullable(email);
    }
}
