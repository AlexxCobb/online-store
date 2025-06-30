package ru.zinovev.online.store.dao.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.zinovev.online.store.controller.dto.AddressUpdateDto;
import ru.zinovev.online.store.dao.entity.DeliveryAddress;
import ru.zinovev.online.store.model.AddressDetails;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "publicAddressId", source = "publicDeliveryAddressId")
    AddressDetails toAddressDetails(DeliveryAddress deliveryAddress);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
                 nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateAddressFromAddressUpdateDto(@MappingTarget DeliveryAddress deliveryAddress,
                                           AddressUpdateDto addressUpdateDto);
}
