package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.dao.entity.DeliveryAddress;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;
import ru.zinovev.online.store.dao.mapper.AddressMapper;
import ru.zinovev.online.store.dao.repository.AddressRepository;
import ru.zinovev.online.store.dao.repository.AddressTypeRepository;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.AddressDetails;
import ru.zinovev.online.store.model.AddressUpdateDetails;
import ru.zinovev.online.store.model.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressDaoService {

    private final AddressRepository addressRepository;
    private final UserDaoService userDaoService;
    private final AddressTypeRepository addressTypeRepository;
    private final AddressMapper addressMapper;

    @Transactional
    public AddressDetails addAddress(UserDetails userDetails, AddressDetails addressDetails) {
        var user = userDaoService.getByPublicId(userDetails.publicUserId());
        var addressType = addressTypeRepository.getByName(AddressTypeName.USER_ADDRESS);
        var address = DeliveryAddress.builder()
                .publicDeliveryAddressId(UUID.randomUUID().toString())
                .addressType(addressType)
                .user(user)
                .country(addressDetails.country())
                .town(addressDetails.town())
                .zipCode(addressDetails.zipCode())
                .street(addressDetails.street())
                .houseNumber(addressDetails.houseNumber())
                .flatNumber(addressDetails.flatNumber() != null ? addressDetails.flatNumber() : null)
                .isActive(false)
                .isSystem(false)
                .build();
        return addressMapper.toAddressDetails(addressRepository.save(address));
    }

    @Transactional
    public AddressDetails addSystemAddress(AddressDetails addressDetails, AddressTypeName addressTypeName) {
        var addressType = addressTypeRepository.getByName(addressTypeName);
        var address = DeliveryAddress.builder()
                .publicDeliveryAddressId(UUID.randomUUID().toString())
                .addressType(addressType)
                .country(addressDetails.country())
                .town(addressDetails.town())
                .zipCode(addressDetails.zipCode())
                .street(addressDetails.street())
                .houseNumber(addressDetails.houseNumber())
                .isActive(true)
                .isSystem(true)
                .build();
        return addressMapper.toAddressDetails(addressRepository.save(address));
    }

    @Transactional
    public AddressDetails updateAddress(AddressDetails addressDetails,
                                        AddressUpdateDetails addressUpdateDetails) {
        var deliveryAddress = addressRepository.findByPublicDeliveryAddressId(addressDetails.publicAddressId())
                .orElseThrow(() -> new NotFoundException(
                        "Address with id - " + addressDetails.publicAddressId() + " + not found"));
        addressMapper.updateAddressFromAddressUpdateDetails(addressUpdateDetails, deliveryAddress);
        return addressMapper.toAddressDetails(addressRepository.save(deliveryAddress));
    }

    @Transactional
    public void deleteAddress(AddressDetails addressDetails) {
        var address = addressRepository.findByPublicDeliveryAddressId(addressDetails.publicAddressId())
                .orElseThrow(() -> new NotFoundException(
                        "Address with id - " + addressDetails.publicAddressId() + " + not found"));
        addressRepository.delete(address);
    }

    public Optional<AddressDetails> findByPublicId(String publicAddressId) {
        return addressRepository.findByPublicDeliveryAddressId(publicAddressId).map(addressMapper::toAddressDetails);
    }

    public List<AddressDetails> findUserAddresses(UserDetails userDetails) {
        return addressRepository.findUserAddresses(userDetails.publicUserId())
                .stream()
                .map(addressMapper::toAddressDetails)
                .collect(Collectors.toList());
    }

    public List<AddressDetails> findSystemAddresses(AddressTypeName addressTypeName) {
        return addressRepository.findByAddressTypeNameAndIsActiveAndIsSystem(addressTypeName, true, true)
                .stream()
                .map(addressMapper::toAddressDetails)
                .collect(Collectors.toList());
    }

    public List<AddressDetails> findAllSystemAddresses() {
        return addressRepository.findByIsActiveAndIsSystem(true, true)
                .stream()
                .map(addressMapper::toAddressDetails)
                .collect(Collectors.toList());
    }

    public boolean existUserAddress(String publicAddressId, String publicUserId) {
        return addressRepository.existsByPublicDeliveryAddressIdAndUserPublicUserId(publicAddressId, publicUserId);
    }

    public boolean existSystemAddress(String publicAddressId, AddressTypeName addressTypeName) {
        return addressRepository.existsByPublicDeliveryAddressIdAndAddressTypeName(publicAddressId, addressTypeName);
    }
}
