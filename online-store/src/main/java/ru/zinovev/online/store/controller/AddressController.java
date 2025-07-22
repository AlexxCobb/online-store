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
import ru.zinovev.online.store.dao.mapper.AddressMapper;
import ru.zinovev.online.store.model.AddressDetails;
import ru.zinovev.online.store.service.AddressService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/addresses")
public class AddressController {

    private final AddressService addressService;
    private final AddressMapper addressMapper;

    @PostMapping("/user/{publicUserId}")
    public AddressDetails addAddress(@PathVariable String publicUserId, @Valid AddressDto addressDto) {
        log.debug("Received POST request to add user delivery address with userId - {}, dto - {}", publicUserId,
                  addressDto);
        return addressService.addAddress(publicUserId, addressMapper.toAddressDetails(addressDto));
    }

    @PostMapping("/system")
    public AddressDetails addSystemAddress(@Valid AddressDto addressDto, @RequestParam AddressTypeName name) {
        log.debug("Received POST request to add system address dto - {}, with type - {}", addressDto, name);
        return addressService.addSystemAddress(addressMapper.toAddressDetails(addressDto), name);
    }

    @PatchMapping("/user/{publicUserId}/update/{publicAddressId}")
    public AddressDetails updateAddress(@PathVariable String publicUserId, @PathVariable String publicAddressId,
                                        @Valid AddressUpdateDto addressUpdateDto) {
        log.debug("Received PATCH request to update user's (id - {}) delivery address (id - {}), dto - {}",
                  publicUserId, publicAddressId, addressUpdateDto);
        return addressService.updateAddress(publicUserId, publicAddressId,
                                            addressMapper.toAddressUpdateDetails(addressUpdateDto));
    }

    @PatchMapping("/system/{publicAddressId}")
    public AddressDetails updateSystemAddress(@Valid AddressUpdateDto addressUpdateDto,
                                              @PathVariable String publicAddressId,
                                              @RequestParam AddressTypeName name) {
        log.debug("Received PATCH request to update system address (id - {}), dto - {}, type - {}", publicAddressId,
                  addressUpdateDto, name);
        return addressService.updateSystemAddress(publicAddressId,
                                                  addressMapper.toAddressUpdateDetails(addressUpdateDto), name);
    }

    @GetMapping("/user/{publicUserId}")
    public List<AddressDetails> getAddresses(@PathVariable String publicUserId,
                                             @RequestParam(required = false) AddressTypeName name,
                                             @RequestParam(required = false) Boolean isSystem) {
        log.debug("Received GET request to get system addresses");

        return addressService.getAddresses(publicUserId, name, isSystem);
    }

    @DeleteMapping("/user/{publicUserId}/delete/{publicAddressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable String publicUserId, @PathVariable String publicAddressId) {
        log.debug("Received DELETE request to delete address with id = {} from user id - {}", publicAddressId,
                  publicUserId);
        addressService.deleteAddress(publicUserId, publicAddressId, false);
    }

    @DeleteMapping("/system/{publicAddressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSystemAddress(@PathVariable String publicAddressId) {
        log.debug("Received DELETE request to delete system address with id = {}", publicAddressId);
        addressService.deleteAddress(null, publicAddressId, true);
    }
}
