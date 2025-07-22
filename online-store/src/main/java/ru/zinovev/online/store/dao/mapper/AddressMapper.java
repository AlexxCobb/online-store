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
    AddressDetails toAddressDetails(DeliveryAddress deliveryAddress);

    @Mapping(target = "publicAddressId", ignore = true)
    AddressDetails toAddressDetails(AddressDto addressDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
                 nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    AddressUpdateDetails toAddressUpdateDetails(AddressUpdateDto addressUpdateDto);

    @Mapping(target = "publicAddressId", source = "publicDeliveryAddressId")
    AddressShortDetails toAddressShortDetails(DeliveryAddress deliveryAddress);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
                 nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateAddressFromAddressUpdateDetails(@MappingTarget DeliveryAddress deliveryAddress,
                                               AddressUpdateDetails addressUpdateDetails);
}
