package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;


public record UserUpdateDto(
        @Size(min = 1, max = 250)
        String name,
        @Size(min = 2, max = 250)
        String lastname,
        @Email
        @Size(min = 6, max = 254)
        String email
) {
}
