package ru.zinovev.online.store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zinovev.online.store.dao.AddressDaoService;
import ru.zinovev.online.store.dao.entity.AddressType;
import ru.zinovev.online.store.dao.entity.DeliveryAddress;
import ru.zinovev.online.store.dao.entity.User;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.exception.model.ForbiddenException;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.exception.model.NotValidArgumentException;
import ru.zinovev.online.store.model.AddressDetails;
import ru.zinovev.online.store.model.AddressUpdateDetails;
import ru.zinovev.online.store.model.UserDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressDaoService addressDaoService;
    @Mock
    private UserService userService;
    @InjectMocks
    private AddressService addressService;

    private User mockUser;
    private UserDetails mockUserDetails;
    private AddressType mockUserType;
    private AddressType mockSystemType;
    private DeliveryAddress mockUserAddress;
    private DeliveryAddress mockSystemAddress;
    private AddressDetails mockAddressDetails;
    private AddressUpdateDetails mockAddressUpdateDetails;
    private AddressDetails mockUserAddressDetails;
    private AddressDetails mockSystemDetails;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .publicUserId(UUID.randomUUID().toString())
                .birthday(LocalDate.of(2000, 2, 2))
                .email("ya@ya.ru")
                .name("name")
                .lastname("lastname")
                .passwordHash("passwordEncoder.encode(userRegistrationDto.password())")
                .build();
        mockUserDetails = new UserDetails(mockUser.getPublicUserId(), mockUser.getEmail(), mockUser.getName(),
                                          mockUser.getLastname());
        mockUserType = AddressType.builder()
                .id(1)
                .name(AddressTypeName.USER_ADDRESS)
                .build();
        mockSystemType = AddressType.builder()
                .id(2)
                .name(AddressTypeName.STORE_ADDRESS)
                .build();
        mockAddressDetails =
                new AddressDetails("publicId", null, "Russia", "SPb", 190000, "Nevsky", 1, null, null, null);
        mockAddressUpdateDetails =
                new AddressUpdateDetails(null, "Nekrasov", null, null);
        mockUserAddress = DeliveryAddress.builder()
                .publicDeliveryAddressId(UUID.randomUUID().toString())
                .addressType(mockUserType)
                .user(mockUser)
                .country(mockAddressDetails.country())
                .town(mockAddressDetails.town())
                .zipCode(mockAddressDetails.zipCode())
                .street(mockAddressDetails.street())
                .houseNumber(mockAddressDetails.houseNumber())
                .flatNumber(mockAddressDetails.flatNumber() != null ? mockAddressDetails.flatNumber() : null)
                .isActive(false)
                .isSystem(false)
                .build();
        mockSystemAddress = DeliveryAddress.builder()
                .publicDeliveryAddressId(UUID.randomUUID().toString())
                .addressType(mockSystemType)
                .country(mockAddressDetails.country())
                .town(mockAddressDetails.town())
                .zipCode(mockAddressDetails.zipCode())
                .street(mockAddressDetails.street())
                .houseNumber(mockAddressDetails.houseNumber())
                .isActive(true)
                .isSystem(true)
                .build();

        mockUserAddressDetails =
                new AddressDetails(mockUserAddress.getPublicDeliveryAddressId(), mockUserDetails,
                                   mockUserAddress.getCountry(),
                                   mockUserAddress.getTown(), mockUserAddress.getZipCode(),
                                   mockUserAddress.getStreet(), mockUserAddress.getHouseNumber(),
                                   mockUserAddress.getFlatNumber(), mockUserType.getName(), false);
        mockSystemDetails =
                new AddressDetails(mockSystemAddress.getPublicDeliveryAddressId(), null, mockSystemAddress.getCountry(),
                                   mockSystemAddress.getTown(), mockSystemAddress.getZipCode(),
                                   mockSystemAddress.getStreet(), mockSystemAddress.getHouseNumber(),
                                   mockSystemAddress.getFlatNumber(), mockSystemType.getName(), true);
    }

    @Test
    void shouldAddAddress() {
        when(userService.findUserDetails(mockUserDetails.publicUserId())).thenReturn(mockUserDetails);
        when(addressDaoService.addAddress(mockUserDetails, mockAddressDetails)).thenReturn(mockUserAddressDetails);

        var result = addressService.addAddress(mockUser.getPublicUserId(), mockAddressDetails);

        assertNotNull(result);
        assertEquals(mockUserAddressDetails, result);
    }

    @Test
    void shouldAddSystemAddress() {
        when(addressDaoService.addSystemAddress(mockAddressDetails, mockSystemType.getName())).thenReturn(
                mockSystemDetails);

        var result = addressService.addSystemAddress(mockUser.getPublicUserId(), mockAddressDetails,
                                                     mockSystemType.getName());

        assertNotNull(result);
        assertEquals(mockSystemDetails, result);
        assertEquals(mockSystemAddress.getPublicDeliveryAddressId(), result.publicAddressId());
    }

    @Test
    void shouldThrowExceptionWhenWrongAddressTypeName() {
        assertThrows(ForbiddenException.class,
                     () -> addressService.addSystemAddress(mockUser.getPublicUserId(), mockAddressDetails,
                                                           mockUserType.getName()));
        verify(addressDaoService, never()).addSystemAddress(mockAddressDetails, mockUserType.getName());
    }

    @Test
    void shouldUpdateAddress() {
        when(userService.findUserDetails(mockUserDetails.publicUserId())).thenReturn(mockUserDetails);
        when(addressDaoService.findByPublicId(mockUserAddress.getPublicDeliveryAddressId())).thenReturn(
                Optional.of(mockUserAddressDetails));
        when(addressDaoService.updateAddress(mockUserAddressDetails,
                                             mockAddressUpdateDetails)).thenReturn(mockUserAddressDetails);

        var result =
                addressService.updateAddress(mockUser.getPublicUserId(), mockUserAddress.getPublicDeliveryAddressId(),
                                             mockAddressUpdateDetails);

        assertNotNull(result);
        assertEquals(mockUserAddressDetails, result);
        assertEquals(mockUserAddressDetails.publicAddressId(), result.publicAddressId());
    }

    @Test
    void shouldThrowExceptionWhenWrongUserId() {
        var userDetails = new UserDetails("mockUser.getPublicUserId()", mockUser.getEmail(), mockUser.getName(),
                                          mockUser.getLastname());
        when(userService.findUserDetails(userDetails.publicUserId())).thenReturn(userDetails);
        when(addressDaoService.findByPublicId(mockUserAddress.getPublicDeliveryAddressId())).thenReturn(
                Optional.of(mockUserAddressDetails));

        assertThrows(BadRequestException.class, () -> addressService.updateAddress(userDetails.publicUserId(),
                                                                                   mockUserAddress.getPublicDeliveryAddressId(),
                                                                                   mockAddressUpdateDetails)
        );
        verify(addressDaoService, never()).updateAddress(mockAddressDetails, mockAddressUpdateDetails);
    }

    @Test
    void shouldUpdateSystemAddress() {
        when(addressDaoService.findByPublicId(mockSystemAddress.getPublicDeliveryAddressId())).thenReturn(
                Optional.of(mockSystemDetails));
        when(addressDaoService.updateAddress(mockSystemDetails, mockAddressUpdateDetails)).thenReturn(
                mockSystemDetails);

        var result =
                addressService.updateSystemAddress(mockUser.getPublicUserId(),
                                                   mockSystemAddress.getPublicDeliveryAddressId(),
                                                   mockAddressUpdateDetails);

        assertNotNull(result);
        assertEquals(mockSystemDetails, result);
    }

    @Test
    void shouldGetUserAddresses() {
        when(userService.findUserDetails(mockUserDetails.publicUserId())).thenReturn(mockUserDetails);
        when(addressDaoService.findUserAddresses(mockUserDetails)).thenReturn(List.of(mockUserAddressDetails));

        var result = addressService.getAddresses(mockUser.getPublicUserId(), AddressTypeName.USER_ADDRESS, false);

        assertEquals(1, result.size());
        assertEquals(mockUserAddressDetails, result.get(0));
    }

    @Test
    void shouldGetSystemAddresses() {
        when(userService.findUserDetails(mockUserDetails.publicUserId())).thenReturn(mockUserDetails);
        when(addressDaoService.findAllSystemAddresses()).thenReturn(List.of(mockSystemDetails));

        var result = addressService.getAddresses(mockUser.getPublicUserId(), null, true);

        assertEquals(1, result.size());
        assertEquals(mockSystemDetails, result.get(0));
    }

    @Test
    void shouldGetSystemAddressesWithType() {
        when(userService.findUserDetails(mockUserDetails.publicUserId())).thenReturn(mockUserDetails);
        when(addressDaoService.findSystemAddresses(mockSystemType.getName())).thenReturn(List.of(mockSystemDetails));

        var result = addressService.getAddresses(mockUser.getPublicUserId(), mockSystemType.getName(), true);

        assertEquals(1, result.size());
        assertEquals(mockSystemDetails, result.get(0));
    }

    @Test
    void shouldDeleteUserAddress() {
        when(addressDaoService.findByPublicId(mockUserAddress.getPublicDeliveryAddressId())).thenReturn(
                Optional.of(mockUserAddressDetails));
        when(userService.findUserDetails(mockUserDetails.publicUserId())).thenReturn(mockUserDetails);

        addressService.deleteAddress(mockUser.getPublicUserId(), mockUserAddress.getPublicDeliveryAddressId(), false);

        verify(addressDaoService, times(1)).deleteAddress(mockUserAddressDetails);
    }

    @Test
    void shouldDeleteSystemAddress() {
        when(addressDaoService.findByPublicId(mockSystemAddress.getPublicDeliveryAddressId())).thenReturn(
                Optional.of(mockSystemDetails));

        addressService.deleteAddress("", mockSystemAddress.getPublicDeliveryAddressId(), true);

        verify(addressDaoService, times(1)).deleteAddress(mockSystemDetails);
    }


    @Test
    void shouldThrowExceptionWhenDeleteWithWrongData() {
        when(addressDaoService.findByPublicId(mockSystemAddress.getPublicDeliveryAddressId())).thenReturn(
                Optional.of(mockSystemDetails));

        assertThrows(NotValidArgumentException.class, () -> addressService.deleteAddress(mockUserDetails.publicUserId(),
                                                                                         mockSystemAddress.getPublicDeliveryAddressId(),
                                                                                         false)
        );
        verify(addressDaoService, never()).deleteAddress(mockSystemDetails);
    }

    @Test
    void shouldThrowExceptionWhenDeleteWithWrongAddressType() {
        var userAddress = new AddressDetails(mockUserAddress.getPublicDeliveryAddressId(), mockUserDetails,
                                             mockUserAddress.getCountry(),
                                             mockUserAddress.getTown(), mockUserAddress.getZipCode(),
                                             mockUserAddress.getStreet(), mockUserAddress.getHouseNumber(),
                                             mockUserAddress.getFlatNumber(), mockSystemType.getName(), false);

        when(addressDaoService.findByPublicId(userAddress.publicAddressId())).thenReturn(
                Optional.of(userAddress));
        when(userService.findUserDetails(mockUserDetails.publicUserId())).thenReturn(mockUserDetails);

        assertThrows(BadRequestException.class, () -> addressService.deleteAddress(mockUserDetails.publicUserId(),
                                                                                   userAddress.publicAddressId(),
                                                                                   false)
        );
        verify(addressDaoService, never()).deleteAddress(userAddress);
    }

    @Test
    void shouldReturnTrueWhenUserAddressExist() {
        when(addressDaoService.findByPublicId(mockUserAddress.getPublicDeliveryAddressId())).thenReturn(
                Optional.of(mockUserAddressDetails));
        when(addressDaoService.existUserAddress(mockUserAddress.getPublicDeliveryAddressId(),
                                                mockUser.getPublicUserId())).thenReturn(true);

        assertTrue(addressService.existUserAddress(mockUserAddress.getPublicDeliveryAddressId(),
                                                   mockUser.getPublicUserId()));
    }

    @Test
    void shouldThrowExceptionWhenAddressNotFound() {
        when(addressDaoService.findByPublicId(mockUserAddress.getPublicDeliveryAddressId())).thenReturn(
                Optional.empty());

        assertThrows(NotFoundException.class,
                     () -> addressService.existUserAddress(mockUserAddress.getPublicDeliveryAddressId(),
                                                           mockUser.getPublicUserId()));
        verify(addressDaoService, never()).existUserAddress(mockUserAddress.getPublicDeliveryAddressId(),
                                                            mockUser.getPublicUserId());
    }
}