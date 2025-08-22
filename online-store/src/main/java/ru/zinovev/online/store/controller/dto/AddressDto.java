package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressDto(
        @NotBlank(message = "Укажите страну")
        @Size(min = 3, max = 100)
        String country,
        @NotBlank(message = "Укажите город")
        @Size(min = 3, max = 100)
        String town,
        @NotBlank(message = "Укажите Индекс")
        @Pattern(regexp = "\\d{6}", message = "Индекс должен содержать шесть цифр")
        @Size(min = 6, max = 6)
        String zipCode,
        @NotBlank(message = "Укажите улицу/проспект")
        @Size(min = 3, max = 200)
        String street,
        @NotBlank(message = "Укажите номер дома")
        @Pattern(regexp = "\\d+", message = "Номер дома может быть только числом")
        String houseNumber,
        @Pattern(regexp = "\\d*", message = "Номер квартиры может быть только числом")
        String flatNumber
) {
}
