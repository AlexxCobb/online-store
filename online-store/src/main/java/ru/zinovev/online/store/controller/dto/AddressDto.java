package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AddressDto(
        @NotBlank(message="Country is required")
        @Size(min = 3, max = 100)
        String country,
        @NotBlank(message="Town is required")
        @Size(min = 3, max = 100)
        String town,
        @NotBlank(message="Zip code is required")
        @Positive
        @Size(min = 6, max = 6)
        Integer zipCode,
        @NotBlank(message = "Street is required")
        @Size(min = 3, max = 200)
        String street,
        @NotBlank(message="House number is required")
        @Positive
        Integer houseNumber,
        @Positive
        Integer flatNumber
) {
}
