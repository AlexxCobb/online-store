package ru.zinovev.online.store.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.dao.AddressDaoService;
import ru.zinovev.online.store.dao.entity.DeliveryAddress;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.exception.model.InvalidArgumentException;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.AddressDetails;
import ru.zinovev.online.store.model.AddressUpdateDetails;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressDaoService addressDaoService;
    private final UserService userService;

    public AddressDetails addAddress(@NonNull String publicUserId, @NonNull AddressDetails addressDetails) {
        // разобраться с получением прав admin/user для корректного сохранения адреса (security)
        var userDetails = userService.findUserDetails(publicUserId);
        return addressDaoService.addAddress(userDetails, addressDetails);
    }

    public AddressDetails addSystemAddress(@NonNull String publicUserId, @NonNull AddressDetails addressDetails,
                                           @NonNull AddressTypeName addressTypeName) {
        userService.findUserDetails(publicUserId);
        if (addressTypeName.equals(AddressTypeName.USER_ADDRESS)) {
            throw new BadRequestException("Administrator cannot create addresses of type USER_ADDRESS"); //403
        }
        return addressDaoService.addSystemAddress(addressDetails, addressTypeName);
    }

    public AddressDetails updateAddress(@NonNull String publicUserId, @NonNull String publicAddressId,
                                        @NonNull AddressUpdateDetails addressUpdateDetails) {
        var userDetails = userService.findUserDetails(publicUserId);
        var address = findByPublicId(publicAddressId);
        if (!userDetails.publicUserId().equals(address.getUser().getPublicUserId()) ||
                !address.getAddressType().getName().equals(AddressTypeName.USER_ADDRESS)) {
            throw new BadRequestException(
                    "User with id - " + publicUserId + " cannot edit address with id - " + publicAddressId);
        }
        return addressDaoService.updateAddress(address, addressUpdateDetails);
    }

    public AddressDetails updateSystemAddress(@NonNull String publicAddressId,
                                              @NonNull AddressUpdateDetails addressUpdateDetails,
                                              @NonNull AddressTypeName addressTypeName) {
        var address = findByPublicId(publicAddressId);
        if (!address.getAddressType().getName().equals(addressTypeName)) {
            throw new BadRequestException("In the address found by id - " + publicAddressId
                                                  + " , the address type does not match the one transmitted - "
                                                  + addressTypeName);
        }
        return addressDaoService.updateAddress(address, addressUpdateDetails);
    }

    public List<AddressDetails> getAddresses(@NonNull String publicUserId, AddressTypeName name,
                                             Boolean isSystem) {
        var userDetails = userService.findUserDetails(publicUserId);
        if (name == null && isSystem) {
            return addressDaoService.findAllSystemAddresses();
        } else if (isSystem) {
            return addressDaoService.findSystemAddresses(name);
        } else {
            return addressDaoService.findUserAddresses(userDetails);
        }
    }

    public void deleteAddress(@NonNull String publicUserId, @NonNull String publicAddressId, Boolean isSystem) {
        var address = findByPublicId(publicAddressId);
        if (!isSystem && address.getUser() != null) {
            var userDetails = userService.findUserDetails(publicUserId);
            if (!userDetails.publicUserId().equals(address.getUser().getPublicUserId()) ||
                    !address.getAddressType().getName().equals(AddressTypeName.USER_ADDRESS) || address.getSystem()
                    .equals(true)) {
                throw new BadRequestException(
                        "User with id - " + publicUserId + " cannot delete address with id - " + publicAddressId);
            }
            addressDaoService.deleteAddress(address);
        } else if (isSystem && address.getSystem().equals(true)) {
            addressDaoService.deleteAddress(address);
        } else {
            throw new InvalidArgumentException(
                    "Invalid delete operation parameters - " + publicUserId + publicAddressId + isSystem);
        }
    }

    public boolean existUserAddress(String publicAddressId, String publicUserId) {
        findByPublicId(publicAddressId);
        return addressDaoService.existUserAddress(publicAddressId, publicUserId);
    }

    private DeliveryAddress findByPublicId(String publicAddressId) {
        return addressDaoService.findByPublicId(publicAddressId)
                .orElseThrow(() -> new NotFoundException("Address with id - " + publicAddressId + " + not found"));
    }
}
