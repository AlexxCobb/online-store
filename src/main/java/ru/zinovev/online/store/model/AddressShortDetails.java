package ru.zinovev.online.store.model;

public record AddressShortDetails(
        String publicAddressId,
        String town,
        String street,
        Integer houseNumber,
        Integer flatNumber
) {
}
