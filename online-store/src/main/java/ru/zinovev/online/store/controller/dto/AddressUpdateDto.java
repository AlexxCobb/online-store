package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressUpdateDto(
        @Pattern(regexp = "\\d{6}", message = "Индекс должен содержать шесть цифр")
        @Size(min = 6, max = 6)
        String zipCode,
        @Size(min = 3, max = 200)
        String street,
        @Pattern(regexp = "\\d+", message = "Номер дома может быть только числом")
        String houseNumber,
        @Pattern(regexp = "\\d*", message = "Номер квартиры может быть только числом")
        String flatNumber
) {
}