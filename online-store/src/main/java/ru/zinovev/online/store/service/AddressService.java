package ru.zinovev.online.store.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.dao.AddressDaoService;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.exception.model.ForbiddenException;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.exception.model.NotValidArgumentException;
import ru.zinovev.online.store.model.AddressDetails;
import ru.zinovev.online.store.model.AddressUpdateDetails;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressDaoService addressDaoService;
    private final UserService userService;

    public AddressDetails addAddress(@NonNull String publicUserId, @NonNull AddressDetails addressDetails) {
        var userDetails = userService.findUserDetails(publicUserId);
        return addressDaoService.addAddress(userDetails, addressDetails);
    }

    public AddressDetails addSystemAddress(@NonNull String publicUserId, @NonNull AddressDetails addressDetails,
                                           @NonNull AddressTypeName addressTypeName) {
        userService.findUserDetails(publicUserId);
        if (addressTypeName.equals(AddressTypeName.USER_ADDRESS)) {
            throw new ForbiddenException("Administrator cannot create addresses of type USER_ADDRESS");
        }
        return addressDaoService.addSystemAddress(addressDetails, addressTypeName);
    }

    public AddressDetails updateAddress(@NonNull String publicUserId, @NonNull String publicAddressId,
                                        @NonNull AddressUpdateDetails addressUpdateDetails) {
        var userDetails = userService.findUserDetails(publicUserId);
        var address = getAddressByPublicId(publicAddressId);
        if (!userDetails.publicUserId().equals(address.userDetails().publicUserId()) ||
                !address.addressTypeName().equals(AddressTypeName.USER_ADDRESS.name())) {
            throw new BadRequestException(
                    "User with id - " + publicUserId + " cannot edit address with id - " + publicAddressId);
        }
        return addressDaoService.updateAddress(address, addressUpdateDetails);
    }

    public AddressDetails updateSystemAddress(@NonNull String publicUserId, @NonNull String publicAddressId,
                                              @NonNull AddressUpdateDetails addressUpdateDetails) {
        userService.findUserDetails(publicUserId);
        var address = getAddressByPublicId(publicAddressId);
        return addressDaoService.updateAddress(address, addressUpdateDetails);
    }

    public AddressDetails getAddressByPublicId(@NonNull String publicAddressId) {
        return addressDaoService.findByPublicId(publicAddressId)
                .orElseThrow(() -> new NotFoundException("Address with id - " + publicAddressId + " not found"));
    }

    public List<AddressDetails> getAddresses(@NonNull String publicUserId, AddressTypeName name,
                                             Boolean isSystem) {
        var userDetails = userService.findUserDetails(publicUserId);
        if (name == null && isSystem) {
            return addressDaoService.findAllSystemAddresses();
        } else if (name != null && !name.equals(AddressTypeName.USER_ADDRESS) && isSystem) {
            return addressDaoService.findSystemAddresses(name);
        } else if (name != null && name.equals(AddressTypeName.USER_ADDRESS)) {
            return addressDaoService.findUserAddresses(userDetails);
        } else {
            throw new NotValidArgumentException(
                    "Invalid parameters to get addresses, AddressTypeName :" + name + "system address flag: "
                            + isSystem);
        }
    }

    public void deleteAddress(@NonNull String publicUserId, @NonNull String publicAddressId, Boolean isSystem) {
        var address = getAddressByPublicId(publicAddressId);
        if (!isSystem && address.userDetails() != null) {
            var userDetails = userService.findUserDetails(publicUserId);
            if (!userDetails.publicUserId().equals(address.userDetails().publicUserId()) ||
                    !address.addressTypeName().equals(AddressTypeName.USER_ADDRESS.name()) || address.isSystem()
                    .equals(true)) {
                throw new BadRequestException(
                        "User with id - " + publicUserId + " cannot delete address with id - " + publicAddressId);
            }
            addressDaoService.deleteAddress(address);
        } else if (isSystem && address.isSystem().equals(true)) {
            addressDaoService.deleteAddress(address);
        } else {
            throw new NotValidArgumentException(
                    "Invalid delete operation parameters - " + publicUserId + publicAddressId + isSystem);
        }
    }

    public boolean existUserAddress(String publicAddressId, String publicUserId) {
        userService.findUserDetails(publicUserId);
        getAddressByPublicId(publicAddressId);
        return addressDaoService.existUserAddress(publicAddressId, publicUserId);
    }

    public boolean existSystemAddress(String publicAddressId, AddressTypeName name) {
        getAddressByPublicId(publicAddressId);
        return addressDaoService.existSystemAddress(publicAddressId, name);
    }
}
