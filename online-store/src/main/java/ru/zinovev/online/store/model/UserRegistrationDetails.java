package ru.zinovev.online.store.model;

import java.time.LocalDate;

public record UserRegistrationDetails(
        String name,
        String lastname,
        LocalDate birthday,
        String email,
        String password,
        String confirmPassword
) {
}