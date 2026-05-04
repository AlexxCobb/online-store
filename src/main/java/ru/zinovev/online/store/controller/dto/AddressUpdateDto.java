package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressUpdateDto(

        @Size(max = 6, message = "Индекс должен состоять из 6 цифр")
        @Pattern(regexp = "\\d+", message = "Индекс должен быть числом")
        String zipCode,

        @Size(max = 200)
        String street,

        @Pattern(regexp = "\\d+", message = "Номер дома должен быть числом")
        @Size(max = 10, message = "Слишком длинный номер дома")
        String houseNumber,

        @Pattern(regexp = "\\d+", message = "Номер квартиры должен быть числом")
        @Size(max = 10)
        String flatNumber
) {
}