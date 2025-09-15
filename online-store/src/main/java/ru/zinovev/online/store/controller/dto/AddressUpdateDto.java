package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AddressUpdateDto(
        @Positive(message = "Индекс состоит из шести положительных цифр")
        @Min(value = 100000, message = "Индекс должен состоять из 6 цифр")
        @Max(value = 999999, message = "Индекс должен состоять из 6 цифр")
        Integer zipCode,
        @Size(max = 200)
        String street,
        @Positive(message = "Номер дома может быть только положительным числом")
        Integer houseNumber,
        @Positive(message = "Номер квартиры может быть только положительным числом")
        Integer flatNumber
) {
}