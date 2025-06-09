package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zinovev.online.store.dao.entity.DeliveryAddress;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
}
