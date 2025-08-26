package ru.zinovev.online.store.model;

public record AddressDetails(
        String publicAddressId,
        UserDetails userDetails,
        String country,
        String town,
        Integer zipCode,
        String street,
        Integer houseNumber,
        Integer flatNumber,
        String addressTypeName,
        Boolean isSystem
) {
}
