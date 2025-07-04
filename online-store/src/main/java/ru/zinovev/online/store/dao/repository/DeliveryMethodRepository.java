package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zinovev.online.store.dao.entity.DeliveryMethod;
import ru.zinovev.online.store.dao.entity.enums.DeliveryMethodName;

public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethod, Integer> {

    DeliveryMethod getByName(DeliveryMethodName name);
}
