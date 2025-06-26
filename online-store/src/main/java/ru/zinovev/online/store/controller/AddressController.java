package ru.zinovev.online.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.zinovev.online.store.controller.dto.AddressDto;
import ru.zinovev.online.store.controller.dto.AddressUpdateDto;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;
import ru.zinovev.online.store.model.AddressDetails;
import ru.zinovev.online.store.service.AddressService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/addresses")
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/user/{publicUserId}")
    public AddressDetails addAddress(@PathVariable String publicUserId, @Valid AddressDto addressDto) {
        log.debug("Received POST request to add user delivery address");
        return addressService.addAddress(publicUserId, addressDto);
    }

    @PostMapping("/store")
    public AddressDetails addStoreAddress(@Valid AddressDto addressDto) {
        log.debug("Received POST request to add store address");
        return addressService.addSystemAddress(addressDto, AddressTypeName.STORE_ADDRESS);
    }

    @PostMapping("/locker")
    public AddressDetails addLockerAddress(@Valid AddressDto addressDto) {
        log.debug("Received POST request to add locker address");
        return addressService.addSystemAddress(addressDto, AddressTypeName.PARCEL_LOCKER);
    }

    @PatchMapping("/user/{publicUserId}/update/{publicAddressId}")
    public AddressDetails updateAddress(@PathVariable String publicUserId, @PathVariable String publicAddressId,
                                        @Valid AddressUpdateDto addressUpdateDto) {
        log.debug("Received PATCH request to update user's delivery address");
        return addressService.updateAddress(publicUserId, publicAddressId, addressUpdateDto);
    }

    @GetMapping("/user/{publicUserId}")
    public List<AddressDetails> getUserAddresses (@PathVariable String publicUserId){
        log.debug("Received GET request to get user addresses");
        return addressService.getUserAddresses(publicUserId);
    }

    @GetMapping("/user/store")
    public List<AddressDetails> getStoreAddresses (){
        log.debug("Received GET request to get store addresses");
        return addressService.getSystemAddresses(AddressTypeName.STORE_ADDRESS);
    }

    @GetMapping("/user/locker")
    public List<AddressDetails> getLockerAddresses (){
        log.debug("Received GET request to get locker addresses");
        return addressService.getSystemAddresses(AddressTypeName.PARCEL_LOCKER);
    }
}
