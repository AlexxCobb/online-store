package ru.zinovev.online.store.dao.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.zinovev.online.store.controller.dto.AddressDto;
import ru.zinovev.online.store.controller.dto.AddressUpdateDto;
import ru.zinovev.online.store.dao.entity.DeliveryAddress;
import ru.zinovev.online.store.model.AddressDetails;
import ru.zinovev.online.store.model.AddressShortDetails;
import ru.zinovev.online.store.model.AddressUpdateDetails;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "publicAddressId", source = "publicDeliveryAddressId")
    @Mapping(target = "userDetails", source = "user")
    @Mapping(target = "addressTypeName", source = "addressType.name")
    AddressDetails toAddressDetails(DeliveryAddress deliveryAddress);

    @Mapping(target = "publicAddressId", ignore = true)
    AddressDetails toAddressDetails(AddressDto addressDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
                 nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "zipCode", expression = "java(convertToInteger(addressUpdateDto.zipCode()))")
    @Mapping(target = "houseNumber", expression = "java(convertToInteger(addressUpdateDto.houseNumber()))")
    @Mapping(target = "flatNumber", expression = "java(convertToInteger(addressUpdateDto.flatNumber()))")
    @Mapping(target = "street", expression = "java(checkValue(addressUpdateDto.street()))")
    AddressUpdateDetails toAddressUpdateDetails(AddressUpdateDto addressUpdateDto);

    @Mapping(target = "publicAddressId", source = "publicDeliveryAddressId")
    AddressShortDetails toAddressShortDetails(DeliveryAddress deliveryAddress);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
                 nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateAddressFromAddressUpdateDetails(AddressUpdateDetails addressUpdateDetails, @MappingTarget DeliveryAddress deliveryAddress);

    default Integer convertToInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Integer.parseInt(value);
    }

    default String checkValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value;
    }
}


