package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.zinovev.online.store.dao.entity.DeliveryAddress;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<DeliveryAddress, Long> {

    Optional<DeliveryAddress> findByPublicDeliveryAddressId(String publicDeliveryAddressId);

    @Query("select d from DeliveryAddress d join d.user u join d.addressType ad where u.publicUserId = :publicUserId and ad.name = USER_ADDRESS")
    List<DeliveryAddress> findUserAddresses(String publicUserId);

    List<DeliveryAddress> findByAddressTypeNameAndIsActiveAndIsSystem(AddressTypeName name, Boolean isActive,
                                                                      Boolean isSystem); // зашить в Query active/system?


    List<DeliveryAddress> findByIsActiveAndIsSystem(Boolean isActive, Boolean isSystem);

    boolean existsByPublicDeliveryAddressIdAndUserPublicUserId(String publicDeliveryAddressId, String publicUserId);

    boolean existsByPublicDeliveryAddressIdAndAddressTypeName(String publicDeliveryAddressId, AddressTypeName name);
}
