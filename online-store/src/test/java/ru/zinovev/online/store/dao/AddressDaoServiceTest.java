package ru.zinovev.online.store.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zinovev.online.store.dao.entity.AddressType;
import ru.zinovev.online.store.dao.entity.DeliveryAddress;
import ru.zinovev.online.store.dao.entity.User;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;
import ru.zinovev.online.store.dao.mapper.AddressMapper;
import ru.zinovev.online.store.dao.repository.AddressRepository;
import ru.zinovev.online.store.dao.repository.AddressTypeRepository;
import ru.zinovev.online.store.model.AddressDetails;
import ru.zinovev.online.store.model.AddressUpdateDetails;
import ru.zinovev.online.store.model.UserDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressDaoServiceTest {

    @Mock
    private AddressRepository addressRepository;
    @Mock
    private UserDaoService userDaoService;
    @Mock
    private AddressTypeRepository addressTypeRepository;
    @Mock
    private AddressMapper addressMapper;
    @InjectMocks
    private AddressDaoService addressDaoService;
    @Captor
    private ArgumentCaptor<DeliveryAddress> addressCaptor;

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
        mockAddressDetails = new AddressDetails("publicId", "Russia", "SPb", 190000, "Nevsky", 1, null);
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
                .active(false)
                .system(false)
                .build();
        mockSystemAddress = DeliveryAddress.builder()
                .publicDeliveryAddressId(UUID.randomUUID().toString())
                .addressType(mockSystemType)
                .country(mockAddressDetails.country())
                .town(mockAddressDetails.town())
                .zipCode(mockAddressDetails.zipCode())
                .street(mockAddressDetails.street())
                .houseNumber(mockAddressDetails.houseNumber())
                .active(true)
                .system(true)
                .build();

        mockUserAddressDetails =
                new AddressDetails(mockUserAddress.getPublicDeliveryAddressId(), mockUserAddress.getCountry(),
                                   mockUserAddress.getTown(), mockUserAddress.getZipCode(),
                                   mockUserAddress.getStreet(), mockUserAddress.getHouseNumber(),
                                   mockUserAddress.getFlatNumber());
        mockSystemDetails =
                new AddressDetails(mockSystemAddress.getPublicDeliveryAddressId(), mockSystemAddress.getCountry(),
                                   mockSystemAddress.getTown(), mockSystemAddress.getZipCode(),
                                   mockSystemAddress.getStreet(), mockSystemAddress.getHouseNumber(),
                                   mockSystemAddress.getFlatNumber());
    }


    @Test
    void shouldAddAddress() {
        when(userDaoService.getByPublicId(mockUserDetails.publicUserId())).thenReturn(mockUser);
        when(addressTypeRepository.getByName(mockUserType.getName())).thenReturn(mockUserType);
        when(addressMapper.toAddressDetails(mockUserAddress)).thenReturn(mockUserAddressDetails);
        when(addressRepository.save(addressCaptor.capture())).thenReturn(mockUserAddress);

        var result = addressDaoService.addAddress(mockUserDetails, mockAddressDetails);

        assertNotNull(result);
        assertEquals(mockUserAddressDetails, result);
        assertEquals(mockUser, addressCaptor.getValue().getUser());
    }

    @Test
    void shouldAddSystemAddress() {
        when(addressTypeRepository.getByName(mockSystemType.getName())).thenReturn(mockSystemType);
        when(addressMapper.toAddressDetails(mockSystemAddress)).thenReturn(mockSystemDetails);
        when(addressRepository.save(addressCaptor.capture())).thenReturn(mockSystemAddress);

        var result = addressDaoService.addSystemAddress(mockAddressDetails, mockSystemType.getName());

        assertNotNull(result);
        assertEquals(mockSystemDetails, result);
        assertEquals(mockSystemAddress.getSystem(), addressCaptor.getValue().getSystem());
    }

    @Test
    void shouldUpdateAddress() {
        var updateAddress = DeliveryAddress.builder()
                .street("Nekrasov").build();
        when(addressMapper.toAddressDetails(updateAddress)).thenReturn(mockUserAddressDetails);
        when(addressRepository.save(addressCaptor.capture())).thenReturn(updateAddress);

        var result = addressDaoService.updateAddress(mockUserAddress, mockAddressUpdateDetails);

        assertNotNull(result);
        assertEquals(mockUserAddressDetails, result);
        verify(addressMapper, times(1)).updateAddressFromAddressUpdateDetails(mockUserAddress,
                                                                              mockAddressUpdateDetails);
    }

    @Test
    void shouldFindByPublicId() {
        var publicId = mockSystemAddress.getPublicDeliveryAddressId();
        when(addressRepository.findByPublicDeliveryAddressId(publicId)).thenReturn(Optional.of(mockSystemAddress));

        var result = addressDaoService.findByPublicId(publicId);

        assertTrue(result.isPresent());
        assertEquals(mockSystemAddress, result.get());
    }

    @Test
    void shouldFindUserAddresses() {
        when(addressRepository.findUserAddresses(mockUserDetails.publicUserId())).thenReturn(List.of(mockUserAddress));
        when(addressMapper.toAddressDetails(mockUserAddress)).thenReturn(mockUserAddressDetails);

        var result = addressDaoService.findUserAddresses(mockUserDetails);

        assertEquals(1, result.size());
        assertEquals(mockUserAddressDetails, result.get(0));
    }

    @Test
    void shouldFindSystemAddresses() {
        when(addressRepository.findByAddressTypeNameAndActiveAndSystem(mockSystemType.getName(), true,
                                                                       true)).thenReturn(List.of(mockSystemAddress));
        when(addressMapper.toAddressDetails(mockSystemAddress)).thenReturn(mockSystemDetails);

        var result = addressDaoService.findSystemAddresses(mockSystemType.getName());

        assertEquals(1, result.size());
        assertEquals(mockSystemDetails, result.get(0));
    }

    @Test
    void shouldFindAllSystemAddresses() {
        var systemAddress = DeliveryAddress.builder().build();
        var systemDetails = mockSystemDetails;
        when(addressRepository.findByActiveAndSystem(true, true)).thenReturn(List.of(mockSystemAddress, systemAddress));
        when(addressMapper.toAddressDetails(mockSystemAddress)).thenReturn(mockSystemDetails);
        when(addressMapper.toAddressDetails(systemAddress)).thenReturn(systemDetails);

        var result = addressDaoService.findAllSystemAddresses();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(mockSystemDetails, systemDetails)));
    }
}