package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;


public record UserUpdateDto(
        @Size(max = 250)
        String name,
        @Size(max = 250)
        String lastname,
        @Email
        @Size(max = 254)
        String email
) {
}
