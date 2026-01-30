package ru.zinovev.online.store.model;

public record AddressUpdateDetails(
        Integer zipCode,
        String street,
        Integer houseNumber,
        Integer flatNumber
) {
}
