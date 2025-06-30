package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Optional;

public record AddressUpdateDto(
        @Positive
        @Size(min = 6, max = 6)
        Optional<Integer> zipCode,
        @Size(min = 3, max = 200)
        Optional<String> street,
        @Positive
        Optional<Integer> houseNumber,
        @Positive
        Optional<Integer> flatNumber
) {
}
