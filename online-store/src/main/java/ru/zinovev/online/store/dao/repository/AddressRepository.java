package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.zinovev.online.store.dao.entity.DeliveryAddress;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<DeliveryAddress, Long> {

    Optional<DeliveryAddress> findByPublicDeliveryAddressId(String publicDeliveryAddressId);

    @Query("select d from DeliveryAddress d join User u join AddressType ad where u.publicUserId = :publicUserId and ad.name = USER_ADDRESS")
    List<DeliveryAddress> findUserAddresses(String publicUserId);

    List<DeliveryAddress> findByAddressTypeNameAndActiveAndSystem(AddressTypeName name, Boolean active,
                                                                  Boolean system); // зашить в Query active/system?

    List<DeliveryAddress> findByActiveAndSystem(Boolean active, Boolean system);

    boolean existsByPublicDeliveryAddressIdAndUserPublicUserId(String publicDeliveryAddressId, String publicUserId);

    boolean existsByPublicDeliveryAddressIdAndAddressTypeName(String publicDeliveryAddressId, AddressTypeName name);
}
