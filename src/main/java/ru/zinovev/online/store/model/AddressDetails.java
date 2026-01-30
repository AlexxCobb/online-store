package ru.zinovev.online.store.model;

import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;

public record AddressDetails(
        String publicAddressId,
        UserDetails userDetails,
        String country,
        String town,
        Integer zipCode,
        String street,
        Integer houseNumber,
        Integer flatNumber,
        AddressTypeName addressTypeName,
        Boolean isSystem
) {
}
