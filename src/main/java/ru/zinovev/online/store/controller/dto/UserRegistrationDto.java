package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.zinovev.online.store.controller.validator.ValidBirthDate;

import java.time.LocalDate;

public record UserRegistrationDto(
        @NotBlank(message = "Укажите имя")
        @Size(min = 1, max = 250)
        String name,
        @NotBlank(message = "Укажите фамилию")
        @Size(min = 2, max = 250)
        String lastname,
        @NotNull(message = "Укажите свою дату рождения")
        @ValidBirthDate
        LocalDate birthday,
        @NotBlank(message = "Укажите email")
        @Email(message = "Некорректный формат email")
        @Size(min = 6, max = 254, message = "Название должно содержать от {min} до {max} символов")
        String email,
        @NotBlank(message = "Пароль обязателен!")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
                message = "Пароль должен содержать не менее 8 символов: одну заглавную букву, одну строчную букву и одну цифру."
        )
        String password,
        @NotBlank(message = "Повторите пароль!")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
                message = "Пароль должен содержать не менее 8 символов: одну заглавную букву, одну строчную букву и одну цифру."
        )
        String confirmPassword
) {
}