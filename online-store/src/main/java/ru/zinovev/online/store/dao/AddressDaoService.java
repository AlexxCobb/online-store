package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.zinovev.online.store.controller.dto.AddressDto;
import ru.zinovev.online.store.controller.dto.AddressUpdateDto;
import ru.zinovev.online.store.dao.entity.DeliveryAddress;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;
import ru.zinovev.online.store.dao.mapper.AddressMapper;
import ru.zinovev.online.store.dao.repository.AddressRepository;
import ru.zinovev.online.store.dao.repository.AddressTypeRepository;
import ru.zinovev.online.store.model.AddressDetails;
import ru.zinovev.online.store.model.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AddressDaoService {

    private final AddressRepository addressRepository;
    private final UserDaoService userDaoService;
    private final AddressTypeRepository addressTypeRepository;
    private final AddressMapper addressMapper;

    public AddressDetails addAddress(UserDetails userDetails, AddressDto addressDto) {
        var user = userDaoService.getByPublicId(userDetails.publicUserId());
        var addressType = addressTypeRepository.getByName(AddressTypeName.USER_ADDRESS);
        var address = DeliveryAddress.builder()
                .publicDeliveryAddressId(UUID.randomUUID().toString())
                .addressType(addressType)
                .user(user)
                .country(addressDto.country())
                .town(addressDto.town())
                .zipCode(addressDto.zipCode())
                .street(addressDto.street())
                .houseNumber(addressDto.houseNumber())
                .flatNumber(addressDto.flatNumber() != null ? addressDto.flatNumber() : null)
                .active(false)
                .system(false)
                .build();
        return addressMapper.toAddressDetails(addressRepository.save(address));
    }

    public AddressDetails addSystemAddress(AddressDto addressDto, AddressTypeName addressTypeName) {
        var addressType = addressTypeRepository.getByName(addressTypeName);
        var address = DeliveryAddress.builder()
                .publicDeliveryAddressId(UUID.randomUUID().toString())
                .addressType(addressType)
                .country(addressDto.country())
                .town(addressDto.town())
                .zipCode(addressDto.zipCode())
                .street(addressDto.street())
                .houseNumber(addressDto.houseNumber())
                .active(true)
                .system(true)
                .build();
        return addressMapper.toAddressDetails(addressRepository.save(address));
    }

    public Optional<DeliveryAddress> findByPublicId(String publicAddressId) {
        return addressRepository.findByPublicDeliveryAddressId(publicAddressId);
    }

    public AddressDetails updateAddress(DeliveryAddress deliveryAddress,
                                        AddressUpdateDto addressUpdateDto) {
        addressMapper.updateAddressFromAddressUpdateDto(deliveryAddress, addressUpdateDto);
        return addressMapper.toAddressDetails(addressRepository.save(deliveryAddress));
    }

    public List<AddressDetails> findUserAddresses(UserDetails userDetails) {
        return addressRepository.findUserAddresses(userDetails.publicUserId())
                .stream()
                .map(addressMapper::toAddressDetails)
                .collect(Collectors.toList());
    }

    public List<AddressDetails> findSystemAddresses(AddressTypeName addressTypeName) {
        return addressRepository.findByAddressTypeNameAndActiveAndSystem(addressTypeName, true, true)
                .stream()
                .map(addressMapper::toAddressDetails)
                .collect(Collectors.toList());
    }
}
