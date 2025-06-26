package ru.zinovev.online.store.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.controller.dto.AddressDto;
import ru.zinovev.online.store.controller.dto.AddressUpdateDto;
import ru.zinovev.online.store.dao.AddressDaoService;
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
        return addressDaoService.addSystemAddress(addressDto, addressTypeName);
    }

    public AddressDetails updateAddress(@NonNull String publicUserId, @NonNull String publicAddressId,
                                        @NonNull AddressUpdateDto addressUpdateDto) {
        var userDetails = userService.findUserDetails(publicUserId);
        var address = addressDaoService.findByPublicId(
                        publicAddressId) // подумать так оставить или показывать entity только на dao слое?
                .orElseThrow(() -> new NotFoundException("Address with id - " + publicAddressId + " + not found"));
        if (!userDetails.publicUserId().equals(address.getUser().getPublicUserId()) || !address.getAddressType()
                .getName()
                .equals(AddressTypeName.USER_ADDRESS)) {
            throw new BadRequestException(
                    "User with id - " + publicUserId + " cannot edit address with id - " + publicAddressId);
        }
        return addressDaoService.updateAddress(address, addressUpdateDto);
    }

    public List<AddressDetails> getUserAddresses(String publicUserId) {
        var userDetails = userService.findUserDetails(publicUserId);
        // подумать, бросать здесь исключение или нет, если лист пустой
        return addressDaoService.findUserAddresses(userDetails);
    }

    public List<AddressDetails> getSystemAddresses(AddressTypeName addressTypeName) {
        return addressDaoService.findSystemAddresses(addressTypeName);
    }
}
