package ru.zinovev.online.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @PostMapping("/system")
    public AddressDetails addSystemAddress(@Valid AddressDto addressDto, @RequestParam AddressTypeName name) {
        log.debug("Received POST request to add system address");
        return addressService.addSystemAddress(addressDto, name);
    }

    @PatchMapping("/user/{publicUserId}/update/{publicAddressId}")
    public AddressDetails updateAddress(@PathVariable String publicUserId, @PathVariable String publicAddressId,
                                        @Valid AddressUpdateDto addressUpdateDto) {
        log.debug("Received PATCH request to update user's delivery address");
        return addressService.updateAddress(publicUserId, publicAddressId, addressUpdateDto);
    }

    @PatchMapping("/system/{publicAddressId}")
    public AddressDetails updateSystemAddress(@Valid AddressUpdateDto addressUpdateDto,
                                              @PathVariable String publicAddressId,
                                              @RequestParam AddressTypeName name) {
        log.debug("Received PATCH request to update system address");
        return addressService.updateSystemAddress(publicAddressId, addressUpdateDto, name);
    }

    @GetMapping("/user/{publicUserId}")
    public List<AddressDetails> getUserAddresses(@PathVariable String publicUserId) {
        log.debug("Received GET request to get user addresses");
        return addressService.getUserAddresses(publicUserId);
    }

    @GetMapping("/user/system")
    public List<AddressDetails> getSystemAddresses(@RequestParam(required = false) AddressTypeName name) {
        log.debug("Received GET request to get system addresses");
        if (name == null) {
            return addressService.getAllSystemAddresses();
        }
        return addressService.getSystemAddresses(name);
    }

    @DeleteMapping("/user/{publicUserId}/delete/{publicAddressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable String publicUserId, @PathVariable String publicAddressId) {
        log.debug("Received DELETE request to delete address with id = {} from user id - {}", publicAddressId,
                  publicUserId);
        addressService.deleteAddress(publicUserId, publicAddressId);
    }

    @DeleteMapping("/system/{publicAddressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSystemAddress(@PathVariable String publicAddressId) {
        log.debug("Received DELETE request to delete system address with id = {}", publicAddressId);
        addressService.deleteSystemAddress(publicAddressId);
    }
}
