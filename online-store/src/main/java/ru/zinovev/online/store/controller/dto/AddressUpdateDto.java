package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Optional;

public record AddressUpdateDto(
        @Positive
        @Size(min = 6, max = 6)
        Integer zipCode,
        @Size(min = 3, max = 200)
        String street,
        @Positive
        Integer houseNumber,
        @Positive
        Integer flatNumber
) {
        public Optional<Integer> getOptionalZipCode() {
                return Optional.ofNullable(zipCode);
        }

        public Optional<String> getOptionalStreet() {
                return Optional.ofNullable(street);
        }

        public Optional<Integer> getOptionalHouseNumber() {
                return Optional.ofNullable(houseNumber);
        }

        public Optional<Integer> getOptionalFlatNumber() {
                return Optional.ofNullable(flatNumber);
        }
}
