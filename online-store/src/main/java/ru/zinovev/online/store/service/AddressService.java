package ru.zinovev.online.store.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.controller.dto.AddressDto;
import ru.zinovev.online.store.controller.dto.AddressUpdateDto;
import ru.zinovev.online.store.dao.AddressDaoService;
import ru.zinovev.online.store.dao.entity.DeliveryAddress;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.AddressDetails;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressDaoService addressDaoService;
    private final UserService userService;

    public AddressDetails addAddress(@NonNull String publicUserId, @NonNull AddressDto addressDto) {
        // на будущее разобраться с получением прав admin/user для корректного сохранения адреса
        var userDetails = userService.findUserDetails(publicUserId);
        return addressDaoService.addAddress(userDetails, addressDto);
    }

    public AddressDetails addSystemAddress(@NonNull AddressDto addressDto, @NonNull AddressTypeName addressTypeName) {
        if (addressTypeName.equals(AddressTypeName.USER_ADDRESS)) {
            throw new BadRequestException("Administrator cannot create addresses of type USER_ADDRESS");
        }
        return addressDaoService.addSystemAddress(addressDto, addressTypeName);
    }

    public AddressDetails updateAddress(@NonNull String publicUserId, @NonNull String publicAddressId,
                                        @NonNull AddressUpdateDto addressUpdateDto) {
        var userDetails = userService.findUserDetails(publicUserId);
        var address = getAddressByPublicId(publicAddressId);
        if (!userDetails.publicUserId().equals(address.getUser().getPublicUserId()) ||
                !address.getAddressType().getName().equals(AddressTypeName.USER_ADDRESS)) {
            throw new BadRequestException(
                    "User with id - " + publicUserId + " cannot edit address with id - " + publicAddressId);
        }
        return addressDaoService.updateAddress(address, addressUpdateDto);
    }

    public AddressDetails updateSystemAddress(@NonNull String publicAddressId,
                                              @NonNull AddressUpdateDto addressUpdateDto,
                                              @NonNull AddressTypeName addressTypeName) {
        var address = getAddressByPublicId(publicAddressId);
        if (!address.getAddressType().getName().equals(addressTypeName)) {
            throw new BadRequestException("In the address found by id - " + publicAddressId
                                                  + " , the address type does not match the one transmitted - "
                                                  + addressTypeName);
        }
        return addressDaoService.updateSystemAddress(address, addressUpdateDto);
    }

    public List<AddressDetails> getUserAddresses(String publicUserId) {
        var userDetails = userService.findUserDetails(publicUserId);
        // подумать, бросать здесь исключение или нет, если лист пустой/ на уровне UI написать, что еще нет сохраненных адресов
        return addressDaoService.findUserAddresses(userDetails);
    }

    public List<AddressDetails> getSystemAddresses(AddressTypeName addressTypeName) {
        return addressDaoService.findSystemAddresses(addressTypeName);
    }

    public List<AddressDetails> getAllSystemAddresses() {
        return addressDaoService.findAllSystemAddresses();
    }

    public void deleteAddress(String publicUserId, String publicAddressId) {
        var userDetails = userService.findUserDetails(publicUserId);
        var address = getAddressByPublicId(publicAddressId);
        if (!userDetails.publicUserId().equals(address.getUser().getPublicUserId()) ||
                !address.getAddressType().getName().equals(AddressTypeName.USER_ADDRESS)) {
            throw new BadRequestException(
                    "User with id - " + publicUserId + " cannot delete address with id - " + publicAddressId);
        }
        addressDaoService.deleteAddress(address);
    }


    public void deleteSystemAddress(String publicAddressId) {
        var address = getAddressByPublicId(publicAddressId);
        if (address.getSystem().equals(true)) {
            addressDaoService.deleteAddress(address);
        }
    }

    public DeliveryAddress getAddressByPublicId(String publicAddressId) {
        return addressDaoService.findByPublicId(
                        publicAddressId) // подумать так оставить или показывать entity только на dao слое?
                .orElseThrow(() -> new NotFoundException("Address with id - " + publicAddressId + " + not found"));
    }
}
